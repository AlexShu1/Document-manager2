package com.kylinno.legal.document;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DocumentTest {

    @Test
    public void testUser() {
        String uuid = UUID.randomUUID().toString(); //获取UUID并转化为String对象
        uuid = uuid.replace("-", ""); //因为UUID本身为32位只是生成时多了“-”，所以将它们去点就可
        System.out.println(uuid);
    }

    @Test
    public void testFileSize() {
        BigDecimal fileSize = new BigDecimal(2018000L);
        BigDecimal multiple = new BigDecimal(1024L);
        BigDecimal isInt = new BigDecimal(1L);
        BigDecimal zero = new BigDecimal(0L);

        String[] units = {"KB", "MB", "GB", "TB", "PB"};
        String unit = "B";
        int unitsLength = units.length;

        if(fileSize.compareTo(multiple) > -1) {
            for (int i = 0; i < unitsLength; i++) {
                fileSize = fileSize.divide(multiple);

                if(fileSize.compareTo(multiple) == -1) {
                    unit = units[i];
                    break;
                }
            }
        }

        fileSize = fileSize.setScale(2,   BigDecimal.ROUND_HALF_UP);
        System.out.println(fileSize + unit);
    }
}
