package com.example.myapplication;

//https://www.tutorialspoint.com/android/android_xml_parsers.htm

import android.content.Context;
import android.content.SharedPreferences;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class Xml {


    public Observable<ArrayList<String>> getObservableXml(String ip)
    {
        return Observable.create(new ObservableOnSubscribe<ArrayList<String>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<String>> emitter) throws Exception {
                ArrayList<String> list=getXML(ip);
                if (list!=null && !emitter.isDisposed())
                {
                    emitter.onNext(list);
                    emitter.onComplete();
                }
                else
                {
                    //emitter.onError(new Exception());
                    ArrayList<String> listE=new ArrayList<>();
                    listE.add("error");
                    emitter.onNext(listE);
                }
            }
        });
    }


    public static ArrayList<String> getXML(String ip) {

        try {
            ArrayList<String> urls = new ArrayList<>();

           InputStream input = new URL("http://"+ip+"/channels/channel.xml").openStream();
            //InputStream input=new URL("http://saatmedia.ir:3180/test/channels/channel.xml").openStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(input);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("channel");

            int size = nList.getLength();

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element1 = (Element) node;
                    String url = getValue("url", element1);
                    urls.add(url);
                    System.out.println(url + "majid");
                }
            }
            return urls;
        }
        catch (Exception e)
        {
            e.getMessage();
            return null;
        }
    }

    private static String getValue(String tag,Element element)
    {
        NodeList nodeList=element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node=nodeList.item(0);
        return node.getNodeValue();
    }
}
