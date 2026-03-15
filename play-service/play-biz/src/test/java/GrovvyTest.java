import com.cloud.baowang.play.po.OrderRecordPO;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GrovvyTest {
    public static void main(String[] args) throws IOException {
        Path filePath = Paths.get("/Users/joke/Desktop/code/oversea-2025/play-service/play-biz/src/main/resources/script/Dg2Order.groovy");
        System.err.println("-->"+filePath.toAbsolutePath());
        String groovyScript = Files.readString(filePath, StandardCharsets.UTF_8);

        Logger log = LoggerFactory.getLogger("GroovyScript");

        Binding binding = new Binding();
        binding.setVariable("logger", log);

        GroovyShell shell  = new GroovyShell(binding);
        Script script = shell.parse(groovyScript); // parse 返回 Script
        OrderRecordPO record=new OrderRecordPO();
        record.setVenueType(1);
        String json = "{ \"userName\":\"Utest_60058010\", \"betTime\":\"2025-09-10 12:05:40\", \"winOrLoss\":2000.0, \"winner\":\"巴哈\", \"betDetail\":\"{\\\"andar\\\":1000}\", \"result\":\"{\\\"result\\\":\\\"2,10\\\"}\", \"transfers\":\"[{\\\"amount\\\":4000.0,\\\"serial\\\":\\\"-27288654692\\\"}]\" }";
        Object result = script.invokeMethod("parseGameOrder", new Object[]{json, record});
        System.out.println(result);
    }
}
