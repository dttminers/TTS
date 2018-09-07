/////here in below code use "file(.txt,.pdf,.doc ) path that exists in SDCard in place of
//"imageofFileArayList.get(c1)".means replace "imageofFileArayList.get(c1)" this with file path like"/mnt/sdcard/Sample.pdf"
//
//        if(imageofFileArayList.get(c1)!=null){
//
//        File file = new File(imageofFileArayList.get(c1));
//        String filename=file.getName();
//        Intent intent= new Intent(getActivity(),ShowCaseFileImageActivity.class);
//
//        Uri filetype= Uri.parse(file.getPath());
//        if(filename.endsWith(".gif")||filename.endsWith(".jpg")||filename.endsWith(".png")||filename.endsWith(".jpeg") ||filename.endsWith(".JPEG"))
//        {
//        Log.d("HI1","image view clicked"+c1);
//        ShowCaseFileImageFrag newFragment = new ShowCaseFileImageFrag();
//        Bundle b = new Bundle();
//        b.putString("imageuri",imageofFileArayList.get(c1));
//        newFragment.setArguments(b);
//        Log.d("Details traffic cases frag","is tablet flag"+SharedVariables.isTablet);
//        Log.d("Details traffic cases frag","image path"+imageofFileArayList.get(c1));
//
//        if(SharedVariables.isTablet){/////if u use tablet phone,this code will execute
//        MainMenuScreen objMain=(MainMenuScreen)getActivity();
//        objMain.showDetailsFragment(newFragment);
//        }else{
//        intent= new Intent(getActivity(),ShowCaseFileImageActivity.class);
//        intent.putExtras(b);
//        startActivity(intent);
//        }
//        }///if(.jpg,.gif)
//        else
//        {
//        if(filename.endsWith(".txt") || filename.endsWith(".doc")||filename.endsWith(".DOC") || filename.endsWith(".docx")||filename.endsWith(".DOCX"))
//        {
//        if(filename.endsWith(".txt")){
//        System.out.println("---txt/doc file exists");
//        Uri path = Uri.fromFile(file);
//        Intent intent1= new Intent(Intent.ACTION_VIEW);
//        intent1.setDataAndType(path, "text/html");//---->application/msword
//        startActivity(intent1);
//       /* StringBuilder builder=new StringBuilder();
//        try{
//                  FileInputStream fileIS = new FileInputStream(file);
//                  BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
//                  String readString = new String();
//                  //just reading each line and pass it on the debugger
//                  while((readString = buf.readLine())!= null){
//                   builder.append(readString);
//                     Log.d("line: ", readString);
//                  }
//
//               } catch (FileNotFoundException e) {
//                  e.printStackTrace();
//               } catch (IOException e){
//                  e.printStackTrace();
//               }
//
//        Bundle bundle = new Bundle();
//        bundle.putString("evidencetext",builder.toString());
//        Intent notesIntent = new Intent(getActivity(), ShowEvidenceText.class).putExtras(bundle);
//        startActivity(notesIntent);
//
//       }*/
//        else{
//       /*System.out.println("---txt/doc file exists");
//       Uri path = Uri.fromFile(file);
//       Intent intent1= new Intent(Intent.ACTION_VIEW);
//             intent1.setDataAndType(path, "application/*");//---->application/msword
//             startActivity(intent1);*/
//
//
//           /*//sample code to Display all installed applications in Device
//       boolean thickSoftFlag=false;
//             final PackageManager pm = getActivity().getPackageManager();
//             String str="com.tf.thinkdroid.amlite";
//           //get a list of installed apps.
//                   List<ApplicationInfo> packages = pm
//                           .getInstalledApplications(PackageManager.GET_META_DATA);
//                   for (ApplicationInfo packageInfo : packages) {
//                       Log.d("TAG", "Installed package :" + packageInfo.packageName);
//                       if((packageInfo.packageName).equalsIgnoreCase(str)){
//                        System.out.println("*****s/w installed");
//                        thickSoftFlag=true;}
//                       Log.d("TAG",
//                               "Launch Activity :"
//                                       + pm.getLaunchIntentForPackage(packageInfo.packageName));
//                   }
//             if(thickSoftFlag){*/
//        System.out.println("---Its a doc file");
//        Uri path = Uri.fromFile(file);
//        Intent intent1= new Intent(Intent.ACTION_VIEW);
//        intent1.setDataAndType(path, "application/*");//---->application/msword
//        startActivity(intent1);
//
//            /* }
//             else
//              showAlert("Please Install ThinkFree Office Mobile Viewer Software in Your Device");  */
//
//
//       /*System.out.println("---txt/doc file exists");
//       String extension =
//             MimeTypeMap.getFileExtensionFromUrl(filename);
//             Log.i("extension QQQ ",extension);
//             String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//             System.out.println("MIME TYPE "+mimeType);
//             File fileToRead = new File(filename);
//             Intent i2 = new Intent(Intent.ACTION_VIEW);
//             String type="text/"+extension;
//             System.out.println("TYPE "+type);
//             //i2.setAction(android.content.Intent.ACTION_VIEW);
//             i2.setDataAndType(Uri.fromFile(file), "application/msword");
//             startActivity(i2);*/
//
//        }}
//
//
//        else
//        {
//        if(filename.endsWith(".pdf"))
//        { System.out.println("---pdf file exists");
//
//        Uri path = Uri.fromFile(file);
//        Intent intent1 = new Intent(Intent.ACTION_VIEW);
//        intent1.setDataAndType(path, "application/pdf");
//        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        try {
//        startActivity(intent1);
//        }
//        catch (ActivityNotFoundException e) {
//        Toast.makeText(getActivity(),
//        "No Application Available to View PDF",
//        Toast.LENGTH_SHORT).show();
//        }
//        }
//        }
//        }
//        }//if
//        else{
//        showAlert("There is no content to Display");
//        }
//
//
//
////package in.tts.utils;
////
//////public class DocxToText {
//////}
////public class DocxToText
////{
////    private const string ContentTypeNamespace =
////    @"http://schemas.openxmlformats.org/package/2006/content-types";
////
////    private const string WordprocessingMlNamespace =
////    @"http://schemas.openxmlformats.org/wordprocessingml/2006/main";
////
////    private const string DocumentXmlXPath =
////        "/t:Types/t:Override[@ContentType="" +
////        "application/vnd.openxmlformats-officedocument." +
////        "wordprocessingml.document.main+xml\"]";
////
////    private const string BodyXPath = "/w:document/w:body";
////
////    private string docxFile = "";
////    private string docxFileLocation = "";
////
////    public DocxToText(string fileName)
////    {
////        docxFile = fileName;
////    }
////
////    #region ExtractText()
////    ///
////    /// Extracts text from the Docx file.
////    ///
////    /// Extracted text.
////    public string ExtractText()
////    {
////        if (string.IsNullOrEmpty(docxFile))
////            throw new Exception("Input file not specified.");
////
////        // Usually it is "/word/document.xml"
////
////        docxFileLocation = FindDocumentXmlLocation();
////
////        if (string.IsNullOrEmpty(docxFileLocation))
////            throw new Exception("It is not a valid Docx file.");
////
////        return ReadDocumentXml();
////    }
////    #endregion
////
////    #region FindDocumentXmlLocation()
////    ///
////    /// Gets location of the "document.xml" zip entry.
////    ///
////    /// Location of the "document.xml".
////    private string FindDocumentXmlLocation()
////    {
////        ZipFile zip = new ZipFile(docxFile);
////        foreach (ZipEntry entry in zip)
////        {
////            // Find "[Content_Types].xml" zip entry
////
////            if (string.Compare(entry.Name, "[Content_Types].xml", true) == 0)
////            {
////                Stream contentTypes = zip.GetInputStream(entry);
////
////                XmlDocument xmlDoc = new XmlDocument();
////                xmlDoc.PreserveWhitespace = true;
////                xmlDoc.Load(contentTypes);
////                contentTypes.Close();
////
////                //Create an XmlNamespaceManager for resolving namespaces
////
////                XmlNamespaceManager nsmgr =
////                        new XmlNamespaceManager(xmlDoc.NameTable);
////                nsmgr.AddNamespace("t", ContentTypeNamespace);
////
////                // Find location of "document.xml"
////
////                XmlNode node = xmlDoc.DocumentElement.SelectSingleNode(
////                        DocumentXmlXPath, nsmgr);
////
////                if (node != null)
////                {
////                    string location =
////                            ((XmlElement) node).GetAttribute("PartName");
////                    return location.TrimStart(new char[] {'/'});
////                }
////                break;
////            }
////        }
////        zip.Close();
////        return null;
////    }
////    #endregion
////
////    #region ReadDocumentXml()
////    ///
////    /// Reads "document.xml" zip entry.
////    ///
////    /// Text containing in the document.
////    private string ReadDocumentXml()
////    {
////        StringBuilder sb = new StringBuilder();
////
////        ZipFile zip = new ZipFile(docxFile);
////        foreach (ZipEntry entry in zip)
////        {
////            if (string.Compare(entry.Name, docxFileLocation, true) == 0)
////            {
////                Stream documentXml = zip.GetInputStream(entry);
////
////                XmlDocument xmlDoc = new XmlDocument();
////                xmlDoc.PreserveWhitespace = true;
////                xmlDoc.Load(documentXml);
////                documentXml.Close();
////
////                XmlNamespaceManager nsmgr =
////                        new XmlNamespaceManager(xmlDoc.NameTable);
////                nsmgr.AddNamespace("w", WordprocessingMlNamespace);
////
////                XmlNode node =
////                        xmlDoc.DocumentElement.SelectSingleNode(BodyXPath,nsmgr);
////
////                if (node == null)
////                    return string.Empty;
////
////                sb.Append(ReadNode(node));
////
////                break;
////            }
////        }
////        zip.Close();
////        return sb.ToString();
////    }
////    #endregion
////
////    #region ReadNode()
////    ///
////    /// Reads content of the node and its nested childs.
////    ///
////    /// XmlNode.
////    /// Text containing in the node.
////    private string ReadNode(XmlNode node)
////    {
////        if (node == null || node.NodeType != XmlNodeType.Element)
////            return string.Empty;
////
////        StringBuilder sb = new StringBuilder();
////        foreach (XmlNode child in node.ChildNodes)
////        {
////            if (child.NodeType != XmlNodeType.Element) continue;
////
////            switch (child.LocalName)
////            {
////                case "t":                           // Text
////                    sb.Append(child.InnerText.TrimEnd());
////
////                    string space =
////                            ((XmlElement)child).GetAttribute("xml:space");
////                    if (!string.IsNullOrEmpty(space) &&
////                            space == "preserve")
////                        sb.Append(' ');
////
////                    break;
////
////                case "cr":                          // Carriage return
////                case "br":                          // Page break
////                    sb.Append(Environment.NewLine);
////                    break;
////
////                case "tab":                         // Tab
////                    sb.Append("\t");
////                    break;
////
////                case "p":                           // Paragraph
////                    sb.Append(ReadNode(child));
////                    sb.Append(Environment.NewLine);
////                    sb.Append(Environment.NewLine);
////                    break;
////
////                default:
////                    sb.Append(ReadNode(child));
////                    break;
////            }
////        }
////        return sb.ToString();
////    }
////    #endregion
////}