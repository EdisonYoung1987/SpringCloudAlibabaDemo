package com.edison.springCloudAlibabaDemo.elasticsearch.utils;

import java.util.HashMap;
import java.util.Random;

public class NameUtils {
    FileUtils fileUtils = new FileUtils();

    HashMap<Integer, String> surnameDict = fileUtils.getDict(DictName.SurnameDict);

    HashMap<Integer, String> boynameDict = fileUtils.getDict(DictName.BoynameDict);

    HashMap<Integer, String> grilnameDict = fileUtils.getDict(DictName.GirlnameDict);

    Random rand = new Random();

    //获取随机姓氏

    public String getSurname(){

        return surnameDict.get(Integer.valueOf(rand.nextInt(surnameDict.size())+1));

    }



    //获取随机名字

    public String getName(String sex){

        //生成名字长度1-2

        int nameLength = rand.nextInt(2) + 1;

        String name = "";



        //根据性别从不同的HashMap中随机读取名字

        if (sex.toLowerCase()=="女"){

            for (int i = 0; i < nameLength; i++) {

                name = name + grilnameDict.get(Integer.valueOf(rand.nextInt(grilnameDict.size())+1));

            }

        }else if (sex.toLowerCase()=="男"){

            for (int i = 0; i < nameLength; i++) {

                name = name + boynameDict.get(Integer.valueOf(rand.nextInt(boynameDict.size())+1));

            }

        }

        return name;

    }
}
