package com.cloud.baowang.play.wallet.filter;


import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

public class SBSportRequestWrapper extends HttpServletRequestWrapper {

    protected HttpServletRequest request;

    public SBSportRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ServletInputStream sis = request.getInputStream();
        InputStream is = null;
        String conentEncoding = request.getHeader("Content-Encoding");
        if ("gzip".equalsIgnoreCase(conentEncoding)) {
            is = new GZIPInputStream(sis);
        } else if ("deflate".equalsIgnoreCase(conentEncoding)) {
            is = new DeflaterInputStream(sis);
        } else {
            throw new UnsupportedEncodingException(conentEncoding + " is not supported.");
        }
        final InputStream compressInputStream = is;
        return new ServletInputStream() {
            ReadListener readListener;

            @Override
            public int read() throws IOException {
                int b = compressInputStream.read();
                if (b == -1 && readListener != null) {
                    readListener.onAllDataRead();
                }
                return b;
            }

            @Override
            public boolean isFinished() {
                try {
                    return compressInputStream.available() == 0;
                } catch (IOException e) {
                    if (readListener != null) {
                        readListener.onError(e);
                    }
                    return false;
                }
            }

            @Override
            public boolean isReady() {
                try {
                    return compressInputStream.available() > 0;
                } catch (IOException e) {
                    if (readListener != null) {
                        readListener.onError(e);
                    }
                    return false;
                }
            }

            @Override
            public void setReadListener(final ReadListener readListener) {
                this.readListener = readListener;
                sis.setReadListener(new ReadListener() {
                    @Override
                    public void onDataAvailable() throws IOException {
                        if (readListener != null) {
                            readListener.onDataAvailable();
                        }
                    }

                    @Override
                    public void onAllDataRead() throws IOException {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (readListener != null) {
                            readListener.onError(throwable);
                        }
                    }
                });
            }
        };
    }
}