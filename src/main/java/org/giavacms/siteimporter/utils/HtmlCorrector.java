package org.giavacms.siteimporter.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.giavacms.siteimporter.model.Page;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class HtmlCorrector
{
   public static Document indent(String xml) throws DocumentException, IOException
   {
      Document doc = (Document) DocumentHelper.parseText(xml);
      StringWriter sw = new StringWriter();
      OutputFormat format = OutputFormat.createPrettyPrint();
      XMLWriter xw = new XMLWriter(sw, format);
      xw.write(doc);
      System.out.println(sw.toString());
      return doc;
   }

   public static Document correggiLinks(Document doc, Map<String, Page> pages, String host)
   {
      Elements links = doc.select("a[href]");
      for (Element link : links)
      {
         String linkSimple = link.attr("abs:href");
         // System.out.println(linkSimple);
         if (linkSimple.contains("#"))
            linkSimple = linkSimple.substring(0, linkSimple.lastIndexOf("#"));
         if (linkSimple.contains("?"))
            linkSimple = linkSimple.substring(0, linkSimple.lastIndexOf("?"));
         if (linkSimple.startsWith(host) && pages.containsKey(linkSimple))
         {
            Page page = pages.get(linkSimple);
            link.attr("href", page.getInternalUrl());
         }
      }
      return doc;
   }

   public static String replaceNotPermitted(String content)
   {
      return content.replaceAll("&nbsp;", "&#160;")
               .replaceAll("&iexcl;", "&#161;")
               .replaceAll("&cent;", "&#162;")
               .replaceAll("&pound;", "&#163;")
               .replaceAll("&curren;", "&#164;")
               .replaceAll("&yen;", "&#165;")
               .replaceAll("&brvbar;", "&#166;")
               .replaceAll("&sect;", "&#167;")
               .replaceAll("&uml;", "&#168;")
               .replaceAll("&copy;", "&#169;")
               .replaceAll("&ordf;", "&#170;")
               .replaceAll("&laquo;", "&#171;")
               .replaceAll("&not;", "&#172;")
               .replaceAll("&shy;", "&#173;")
               .replaceAll("&reg;", "&#174;")
               .replaceAll("&macr;", "&#175;")
               .replaceAll("&deg;", "&#176;")
               .replaceAll("&plusmn;", "&#177;")
               .replaceAll("&sup2;", "&#178;")
               .replaceAll("&sup3;", "&#179;")
               .replaceAll("&acute;", "&#180;")
               .replaceAll("&micro;", "&#181;")
               .replaceAll("&para;", "&#182;")
               .replaceAll("&middot;", "&#183;")
               .replaceAll("&cedil;", "&#184;")
               .replaceAll("&sup1;", "&#185;")
               .replaceAll("&ordm;", "&#186;")
               .replaceAll("&raquo;", "&#187;")
               .replaceAll("&frac14;", "&#188;")
               .replaceAll("&frac12;", "&#189;")
               .replaceAll("&frac34;", "&#190;")
               .replaceAll("&iquest;", "&#191;")
               .replaceAll("&times;", "&#215;");
   }

}
