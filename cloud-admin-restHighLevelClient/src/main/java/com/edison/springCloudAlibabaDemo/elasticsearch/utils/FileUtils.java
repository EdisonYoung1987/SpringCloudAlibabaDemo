package com.edison.springCloudAlibabaDemo.elasticsearch.utils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Scanner;

public class FileUtils {
//读取字典

    public HashMap<Integer,String> getDict(String dictName){

        HashMap<Integer, String> map = new HashMap<Integer, String>();

        FileInputStream fis = null;

        try {

            URI uri = this.getClass().getClassLoader().getResource(dictName).toURI();

            File file = new File(uri);

            fis = new FileInputStream(file);

            Scanner scanner = new Scanner(fis);

            while (scanner.hasNextLine()) {

                String[] strings = scanner.nextLine().split("=");

                map.put(Integer.valueOf(strings[0]),strings[1]);

            }

            scanner.close();

            fis.close();

            return map;

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }

}
