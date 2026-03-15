package com.cloud.baowang.common.excel;

import java.util.ArrayList;
import java.util.List;

public class HeaderInfoList<E extends HeaderInfo> extends ArrayList<E> implements List<E> {


    private static final long serialVersionUID = 4090154883064579435L;

    @SuppressWarnings("unchecked")
    public HeaderInfoList<E> add(String name, String alias, Integer columnWidth) {
        return add((Class<E>) HeaderInfo.class, name, alias, columnWidth);
    }

    private HeaderInfoList<E> add(Class<E> clazz, String name, String alias, Integer columnWidth) {

        try {
            E e = clazz.newInstance();
            e.setAlias(alias);
            e.setName(name);
            e.setColumnWidth(columnWidth);
            super.add(e);
        } catch (InstantiationException | IllegalAccessException e1) {
            e1.printStackTrace();
        }

        return this;
    }

//	public static void main(String[] args) {
//		HeaderInfoList<HeaderInfo> list = new HeaderInfoList<>();
//		list.add("name", "alias", 50);
//		
//		List<HeaderInfo> test = list;
//
//        for (int i = 0; i < test.size(); ++i) {
//            HeaderInfo headerInfo = test.get(i);
//
//            String name = headerInfo.getName();
//            String alias = headerInfo.getAlias();
//            int width = headerInfo.getColumnWidth();
//            System.out.print(name);
//        }
//	}
}
