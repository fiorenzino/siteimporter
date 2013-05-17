package org.giavacms.siteimporter.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.giavacms.siteimporter.model.Page;
import org.giavacms.siteimporter.utils.HtmlCorrector;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Example program to list links from a URL.
 */
public class Downloader
{
   static String WEB_SITE = "WB077C7J0";
   // http://wbpreview.com/previews/WB0C4JJ9R/
   static Map<String, Page> pages = new HashMap<String, Page>();
   static String host = "http://wbpreview.com/";
   static String url = "http://wbpreview.com/previews/WB077C7J0/index.html";
   // http://wbpreview.com/previews/WB02634G3/index.html
   static String FOLDER_SITES = "sites/";
   static int i = 0;

   public static void main(String[] args) throws IOException
   {

      print("Fetching %s...", url);
      getLinks(url);
      // http://www.arteinsieme.eu/docs/arte-insieme-collettiva-artisti-sensibili-2012.pdf
      for (Page page : pages.values())
      {
         if (page.getUrl().endsWith("html"))
         {
            System.out.println(page.getUrl() + ": " + page.getInternalUrl());

            File siteFolder = new File(FOLDER_SITES + WEB_SITE + "/");
            siteFolder.mkdir();
            File imgFolder = new File(FOLDER_SITES + WEB_SITE + "/img");
            imgFolder.mkdir();
            File cssFolder = new File(FOLDER_SITES + WEB_SITE + "/css");
            cssFolder.mkdir();
            File embedFolder = new File(FOLDER_SITES + WEB_SITE + "/embed");
            embedFolder.mkdir();
            File jsFolder = new File(FOLDER_SITES + WEB_SITE + "/js");
            jsFolder.mkdir();
            Document doc = correggiAndScarica(page.getDocument(), siteFolder.getAbsolutePath() + "/");

            page.setDocument(doc);
            HtmlCorrector.correggiLinks(doc, pages, host);
            String content = HtmlCorrector.replaceNotPermitted(page.getDocument().html());
            writeOnFile(content, siteFolder.getAbsolutePath() + "/" + page.getInternalUrl());
         }
         else
         {
            System.out.println(page.getUrl() + " - NON RIESCO A SCARICARE LA PAGINA");
         }
      }
   }

   private static void getLinks(String url)
   {
      Document doc = null;
      try
      {
         doc = Jsoup.connect(url).get();
         Page page = new Page(url, doc);
         pages.put(url, page);

         Elements links = doc.select("a[href]");
         for (Element link : links)
         {
            String linkSimple = link.attr("abs:href");
            // System.out.println(linkSimple);
            if (linkSimple.contains("#"))
               linkSimple = linkSimple.substring(0, linkSimple.lastIndexOf("#"));
            if (linkSimple.contains("?"))
               linkSimple = linkSimple.substring(0, linkSimple.lastIndexOf("?"));
            if (linkSimple.startsWith(host) && !pages.containsKey(linkSimple))
            {
               i++;
               // System.out.println(i + ")NEW:" + linkSimple);
               getLinks(linkSimple);
            }
         }
      }
      catch (UnsupportedMimeTypeException e)
      {
         // System.out.println("url no html:" + url);
      }
      catch (IOException e)
      {

      }

   }

   private static boolean writeOnFile(String content, String fileName) throws FileNotFoundException
   {
      PrintWriter out = new PrintWriter(fileName);
      out.println(content);
      out.close();
      return true;
   }

   private static Document correggiAndScarica(Document doc, String baseFolder) throws IOException
   {

      Elements links = doc.select("a[href]");
      Elements media = doc.select("[src]");
      Elements imports = doc.select("link[href]");

      print("\nMedia: (%d)", media.size());
      for (Element src : media)
      {
         if (src.tagName().equals("img"))
         {
            print(" * %s: <%s> %sx%s (%s)",
                     src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
                     trim(src.attr("alt"), 20));
            String newLocalImg = downloadSimple(src.attr("abs:src"), baseFolder, "img/");
            if (newLocalImg != null)
               src.attr("src", newLocalImg);
         }
         else if (src.tagName().equals("embed"))
         {
            String embed = downloadSimple(src.attr("abs:src"), baseFolder, "embed/");
            if (embed != null)
               src.attr("src", embed);
         }
         else if (src.tagName().equals("script") && src.attr("abs:src").contains(host))
         {
            String js = downloadSimple(src.attr("abs:src"), baseFolder, "js/");
            if (js != null)
               src.attr("src", js);
         }
         else
            print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
      }

      print("\nImports: (%d)", imports.size());
      for (Element link : imports)
      {
         print(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"));
         if (link.attr("rel").equals("stylesheet") && link.attr("abs:href").contains(host))
         {
            String newLink = downloadSimple(link.attr("abs:href"), baseFolder, "css/");
            if (newLink != null)
               link.attr("href", newLink);
         }
         else if (link.attr("rel").contains("icon"))
         {
            String newLink = downloadSimple(link.attr("abs:href"), baseFolder, "img/");
            if (newLink != null)
               link.attr("src", newLink);
         }
      }

      print("\nLinks: (%d)", links.size());

      System.out.println("DOPO******************************");
      // System.out.println(doc.toString());
      return doc;
   }

   private static void print(String msg, Object... args)
   {
      System.out.println(String.format(msg, args));
   }

   private static String trim(String s, int width)
   {
      if (s.length() > width)
         return s.substring(0, width - 1) + ".";
      else
         return s;
   }

   public static String downloadSimple(String uri, String baseFolder, String folder) throws IOException
   {
      try
      {
         String fileName = uri.substring(uri.lastIndexOf("/") + 1);
         // lets say saving the png file from google.
         URL url = new URL(uri);
         URLConnection urlConnection = url.openConnection();

         // creating the input stream from google image
         BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
         // my local file writer, output stream
         BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(baseFolder + folder + fileName));

         // until the end of data, keep saving into file.
         int i;
         while ((i = in.read()) != -1)
         {
            out.write(i);
         }
         out.flush();

         // closing all the shits
         out.close();
         in.close();
         return "/" + folder + fileName;
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return null;

   }
}