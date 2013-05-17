package org.giavacms.siteimporter.model;

import org.jsoup.nodes.Document;

public class Page
{
   private String title;
   private String url;
   private Document document;
   private String internalUrl;
   private boolean completed = false;

   public Page()
   {
      // TODO Auto-generated constructor stub
   }

   public Page(String url, Document document)
   {
      this.url = url;
      this.document = document;
      generateInternal();
   }

   public void generateInternal()
   {
      if (this.url.contains("/"))
         this.internalUrl = url.substring(this.url.lastIndexOf("/") + 1);
      if (this.internalUrl.contains("."))
         this.internalUrl = this.internalUrl.substring(0, this.internalUrl.indexOf("."));
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public String getUrl()
   {
      return url;
   }

   public void setUrl(String url)
   {
      this.url = url;
   }

   public String getInternalUrl()
   {
      return internalUrl;
   }

   public void setInternalUrl(String internalUrl)
   {
      this.internalUrl = internalUrl;
   }

   public boolean isCompleted()
   {
      return completed;
   }

   public void setCompleted(boolean completed)
   {
      this.completed = completed;
   }

   public Document getDocument()
   {
      return document;
   }

   public void setDocument(Document document)
   {
      this.document = document;
   }
}
