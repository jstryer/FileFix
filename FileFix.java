/* FileFix.java
 * Jonathan Stryer
 * Last Update: December 11, 2015
 * 
 * FileFix.java converts all files in a given folder to all of the different possible file types.
 * For example, a '.nex' file will also be converted to '.phy' and '.fa'. 
*/

// CHECK TO SEE IF CONVERSIONS ARE CORRECT 

// Prdx.phy

import java.io.*;
import java.util.*;

public class FileFix {
  
  static String workingFolder = "";
  
  public static void main (String[] args) throws IOException {
    
    Scanner input = new Scanner (System.in); // Scanner For User Input
    System.out.println("Enter Name Of Folder Of Files To Convert: ");
    workingFolder = input.nextLine(); // Scans Input For 'workingFolder'
    
    File folder = new File (workingFolder); // Folder With Files
    File[] files = folder.listFiles();   // List of Files In Folder
    
    /* Filtering Out Text Files START */
    for (int i = 0; i < files.length; i++) {
    
      File file = files[i]; // Working File 
      
      // If: File Is A Text File 
      if (file.getName().endsWith(".txt")) {
       
        String temp = file.getName().substring(0, file.getName().indexOf(".txt")); // String temp Holds The Name Of The Working File Without The Extension
        
        Scanner scan = new Scanner (file); // Scanner Object For The Working File 
        String temp2 = scan.nextLine(); // String temp2 Holds The First Line Of The Working File 
        File file2; // New File Object 'file2' Which Will Hold New Pathname
        
        // If: Working File Is A NEXUS File 
        if (temp2.contains("#NEXUS")) { file2 = new File (file.getParent(), temp + ".nex"); }
        // Else-If: Working File Is A FASTA File
        else if (temp2.contains(">")) { file2 = new File (file.getParent(), temp + ".fa"); }
        // Else: Working File Is A PHYLIP File 
        else { file2 = new File (file.getParent(), temp + ".phy"); }
       
        file.renameTo(file2); // Renames The Pathname Of The Working File 
      }
    }
    /* Filtering Out Text Files END */
    
    /* Filtering Out Junk Files START */
    folder = new File (workingFolder); // Folder With Files
    files = folder.listFiles();   // List of Files In Folder
    ArrayList<File> listFiles = new ArrayList<File>(); // Creates A New ArrayList<File> Called 'listFiles' To Store The Non-Junk Files 
    
    // For-Loop To Work Through Each File In 'files'
    for (int i = 0; i < files.length; i++) {
      
      String temp = files[i].getName(); // Sets String 'temp' Equal To The Filename At Index i Of 'files'
      
      // If: Filename Contains More Than Just ".nex"
      if (temp.contains(".nex") && temp.length() > 4) {
        
        listFiles.add(files[i]); // Adds File To 'listFiles'
      }
      // Else-If: Filename Contains More Than Just ".fa"
      else if (temp.contains(".fa") && temp.length() > 3) {
        
        listFiles.add(files[i]); // Adds File To 'listFiles'
      }
      // Else-If: Filename Contains More Than Just ".phy"
      else if (temp.contains(".phy") && temp.length() > 4) {
        
        listFiles.add(files[i]); // Adds File To 'listFiles'
      }
    }
    
    files = listFiles.toArray(new File[listFiles.size()]); // Sets Array 'files' Equal To The Files In 'listFiles'
    /* Filtering Out Junk Files END */
    
    
    System.out.println(Arrays.toString(files));
    
    /* Information From File */
    int ntax  = 0; // Number of Sequences
    int nchar = 0; // Number of Nucleotides In A Sequence
    boolean interleave = false; // Boolean To Check If The File Is Interleaved Or Not 
    ArrayList<String> names = new ArrayList<String>(); // List of Names of Sequences
    ArrayList<String> seqs  = new ArrayList<String>(); // List of Sequences 
    /* Information From File */
    
    //System.out.println(Arrays.toString(files));
    
    /* Loop To Convert Files To NEXUS, PHYLIP, and FASTA */
    for (int i = 0; i < files.length; i++) {
      
      File file = files[i]; // Holds the Working File
      Scanner scan = new Scanner (file); // Scanner For Working File
      String temp = ""; // Working String
     
      System.out.println(file.getName());
      
      /* DATA TAKING PROCESS BEGINS */
      /*---------------------------------------------------------------------------------------------------------------------------------------------------*/
      /* DATA TAKING PROCESS BEGINS */
      
      names.clear(); // Clears the ArrayList 'names' For The New Data Taking
      seqs.clear(); // Clears the ArrayList 'seqs' For The New Data Taking
      interleave = false; 
      
      // IF NEXUS FILE
      if (file.getName().endsWith(".nex")) {
        
        /* Scans For 'ntax' and 'nchar' */
        while (scan.hasNext()) {
          
          temp = scan.next(); // Scans For Next Token And Stores It In 'temp'
          
          if (temp.contains("ntax=") || temp.contains("NTAX") || temp.contains("Ntax")) { ntax = Integer.parseInt(temp.substring(temp.indexOf('=') + 1)); } // Stores Value Of 'ntax'
          if (temp.contains("nchar=") || temp.contains("NCHAR=") || temp.contains("Nchar")) { nchar = Integer.parseInt(temp.substring(temp.indexOf('=') + 1, temp.indexOf(';'))); } // Stores Value Of 'nchar'
          if (temp.compareToIgnoreCase("matrix") == 0) { break; } // Ends The While-Loop When "matrix" Is Found 
        }
        /* Scans For 'ntax' and 'nchar' */
        
        /* Scans For Names and Sequences */
        // For-Loop Scans The First Block Of Data 
        for (int j = 0; j < ntax; j++) {
          
          names.add(scan.next()); // Stores Next Token In 'names' ArrayList 
          seqs.add(scan.next());  // Stores Next Token In 'seqs' ArrayList 
        }
        
        while (scan.hasNext()) {
          
          interleave = true;
          
          temp = scan.next(); // Scans For Next Token And Stores It In 'temp' 
          if (temp.equals(";")) { break; } // Ends The While-Loop When ";" Is Found 
         
          seqs.set(0, seqs.get(0) + scan.next()); // Scans Next Token And Concatenates The Sequence In Appropriate Sequence
          
          for (int j = 1; j < ntax; j++) {
            
            scan.next();
            seqs.set(j, seqs.get(j) + scan.next()); // Scans Next Token And Concatenates The Sequence In Appropriate Sequence
          }
        }
        /* Scans For Names and Sequences */
      }
      
      // IF PHYLIP FILE
      else if (file.getName().endsWith(".phy")) {
        
        ntax = scan.nextInt(); // Stores Value Of 'ntax' 
        nchar = scan.nextInt(); // Stores Value Of 'nchar'
        
        // For-Loop That Stores The Names And Sequences
        for (int j = 0; j < ntax; j++) {
          
          names.add(scan.next()); // Scans Next Token And Adds It To 'names' ArrayList
          seqs.add(scan.next());  // Scans Next Token And Adds It To 'seqs' ArrayList
        }
        
        // While-Loop That Stores The Rest Of The Sequences
        while (scan.hasNext()) {
          
          interleave = true; 
          
          for (int j = 0; j < ntax; j++) {
            
            seqs.set(j, seqs.get(j) + scan.next()); // Scans Next Token And Concatenates It On To Appropiate Index
          }
        }
      }
      
      // IF FASTA FILE
      else if (file.getName().endsWith(".fa")) {
        
        int index = -1; // Working Index
        
        // While-Loop To Work Through FASTA FILE 
        while (scan.hasNextLine()) {
          
          temp = scan.nextLine(); // String 'temp' Stores The Next Line (Working Line)
          
          // If The Working Line Contains ">" (It Is The Name)
          if (temp.contains(">")) {
            
            index++; // Increments Index Counter By 1
            temp = temp.substring(temp.indexOf(">") + 1); // Removes Everything Before And Including ">"
            while (temp.charAt(0) == ' ') { temp = temp.substring(1); } // Removes Leading Whitespace In Name
            names.add(temp); // Adds Modified String 'temp' to 'names'
            temp = scan.nextLine(); // String 'temp' Stores The Next Line 
            seqs.add(temp); // String 'temp' Is Added To 'seqs' 
            
          }
          else {
            
            seqs.set(index, seqs.get(index) + temp);
          }
        }
        
        ntax = names.size(); // Value Of Number Of Sequences Assigned To 'ntax'
        nchar = seqs.get(0).length(); // Value of Number Of Nucleotides In A Sequence Assigned To 'nchar'
      }
      /* DATA TAKING PROCESS ENDS */
      /*---------------------------------------------------------------------------------------------------------------------------------------------------*/
      /* DATA TAKING PROCESS ENDS */
      
      
      /* CONVERSION PROCESS BEGINS */
      /*---------------------------------------------------------------------------------------------------------------------------------------------------*/
      /* CONVERSION PROCESS BEGINS */
      
      // Creates A New Directory For Converted Files
      File directory = new File (workingFolder + "Convert"); 
      directory.mkdir();
      
      // If: The Working File Is Not An Interleaved NEXUS File, It Is Converted To An Interleaved NEXUS File 
      if (!file.getName().endsWith(".nex") || (file.getName().endsWith(".nex") && !interleave)) {
        
        PrintWriter pw = new PrintWriter (workingFolder + "Convert/" + file.getName().substring(0, file.getName().indexOf(".")) + ".nex");
        int whitespace = 0; // Integer To Help Figure Amount Of Whitespace Needed Between Name And Sequence 
        
        pw.println("#NEXUS");
        pw.println();
        pw.println("begin data;");
        pw.println("    dimensions ntax=" + ntax + " nchar=" + nchar +";");
        pw.println("    format datatype=dna interleave=yes gap=- missing=?;");
        pw.print("    matrix");
        
        /* SECTION WORKS TO HAVE CORRECT AMOUNT OF WHITESPACE BETWEEN NAME AND SEQUENCE */
        for (int j = 0; j < names.size(); j++) {
          
          if (names.get(j).length() > whitespace) {
            
            whitespace = names.get(j).length();
          }
        }
        
        whitespace += 4; 
        
        String spaces = "";
        
        while (whitespace > 0) { spaces += " "; whitespace--; }
        
        whitespace = spaces.length();
        /* SECTION WORKS TO HAVE CORRECT AMOUNT OF WHITESPACE BETWEEN NAME AND SEQUENCE */
        
        int index = 0; 
        Boolean bool = true;
        
        // While: There Are Still Nucleotides In The Sequence Left To Print 
        while (bool) {
          
          pw.println();
          
          for (int j = 0; j < names.size(); j++) { 
            
            if (index + 125 > seqs.get(j).length()) { if (seqs.get(j).substring(index).length() > 0) { pw.printf("%." + whitespace + "s%s\n", names.get(j)+spaces, seqs.get(j).substring(index)); } bool = false; }
            else { pw.printf("%." + whitespace + "s%s\n", names.get(j)+spaces, seqs.get(j).substring(index, index+124)); } 
            
          }
          
          pw.println();
          
          index += 125;
        }
        
        pw.println("    ;");
        pw.println("end;");
        
        pw.close();
      }
      
      // If: The Working File Is Not A PHYLIP File, It Is Converted To A PHLYIP File
      if (!file.getName().endsWith(".phy") || (file.getName().endsWith(".phy") && !interleave)) {
        
        PrintWriter pw = new PrintWriter (workingFolder + "Convert/" + file.getName().substring(0, file.getName().indexOf(".")) + ".phy");
        int whitespace = 0; // Integer To Help Figure Amount Of Whitespace Needed Between Name And Sequence 
        
        pw.println(ntax + " " + nchar);
        pw.println();
        
        /* SECTION WORKS TO HAVE CORRECT AMOUNT OF WHITESPACE BETWEEN NAME AND SEQUENCE */
        for (int j = 0; j < names.size(); j++) {
          
          if (names.get(j).length() > whitespace) {
            
            whitespace = names.get(j).length();
          }
        }
        
        whitespace += 4;
        
        String spaces = "";
        
        while (whitespace > 0) { spaces += " "; whitespace--; }
        
        whitespace = spaces.length();
        /* SECTION WORKS TO HAVE CORRECT AMOUNT OF WHITESPACE BETWEEN NAME AND SEQUENCE */
        
        int index = 0; 
        Boolean bool = true;
        
        for (int j = 0; j < names.size(); j++) { 
          
          if (index + 125 > seqs.get(j).length()) { pw.printf("%." + whitespace + "s%s\n", names.get(j)+spaces, seqs.get(j).substring(index)); bool = false; }
          else { pw.printf("%." + whitespace + "s%s\n", names.get(j)+spaces, seqs.get(j).substring(index, index+124)); } 
          
        }
        
        index += 125; 
        
        // While: There Are Still Nucleotides In The Sequence Left To Be Printed 
        while (bool) {
          
          pw.println();
          pw.println();
          
          for (int j = 0; j < names.size(); j++) { 
          
            if (index + 125 > seqs.get(j).length()) { if (seqs.get(j).substring(index).length() > 0) { pw.printf("%s\n", seqs.get(j).substring(index)); } bool = false; }
          else { pw.printf("%s\n", seqs.get(j).substring(index, index+124)); } 
          
        }
          
          index += 125;
        }
        
        pw.close();
      }
      
      // If: The Working File Is Not A FASTA File, It Is Converted To A FASTA File
      if (!file.getName().endsWith(".fa")) {
        
        PrintWriter pw = new PrintWriter (workingFolder + "Convert/" + file.getName().substring(0, file.getName().indexOf(".")) + ".fa");
        
        for (int j = 0; j < names.size(); j++) {
          
          pw.println(">" + names.get(j));
          pw.println(seqs.get(j));
        }
        
        pw.close();
      }
      
      // Converts To A Non-Interleaved NEXUS File 
      if ((file.getName().endsWith(".nex") && interleave) || (!file.getName().endsWith(".nex"))){
        
        PrintWriter pw = new PrintWriter (workingFolder + "Convert/" + file.getName().substring(0, file.getName().indexOf(".")) + "*.nex");
        int whitespace = 0; // Integer To Help Figure Amount Of Whitespace Needed Between Name And Sequence 
        
        pw.println("#NEXUS");
        pw.println();
        pw.println("begin data;");
        pw.println("    dimensions ntax=" + ntax + " nchar=" + nchar +";");
        pw.println("    format datatype=dna interleave=no gap=- missing=?;");
        pw.print("    matrix");
        
        
        /* SECTION WORKS TO HAVE CORRECT AMOUNT OF WHITESPACE BETWEEN NAME AND SEQUENCE */
        for (int j = 0; j < names.size(); j++) {
          
          if (names.get(j).length() > whitespace) {
            
            whitespace = names.get(j).length();
          }
        }
        
        whitespace += 4; 
        
        String spaces = "";
        
        while (whitespace > 0) { spaces += " "; whitespace--; }
        
        whitespace = spaces.length();
        /* SECTION WORKS TO HAVE CORRECT AMOUNT OF WHITESPACE BETWEEN NAME AND SEQUENCE */
        
        pw.println();
        
        for (int j = 0; j < names.size(); j++) { pw.printf("%." + whitespace + "s%s\n", names.get(j)+spaces, seqs.get(j)); }
        
        pw.println("    ;");
        pw.println("end;");
        
        pw.close();
      }
          
      // Converts To A Non-Interleaved PHYLIP File 
      if ((file.getName().endsWith(".phy") && interleave) || (!file.getName().endsWith(".phy"))) {
        
        PrintWriter pw = new PrintWriter (workingFolder + "Convert/" + file.getName().substring(0, file.getName().indexOf(".")) + "*.phy");
        int whitespace = 0; // Integer To Help Figure Amount Of Whitespace Needed Between Name And Sequence 
        
        pw.println(ntax + " " + nchar);
        pw.println();
        
        /* SECTION WORKS TO HAVE CORRECT AMOUNT OF WHITESPACE BETWEEN NAME AND SEQUENCE */
        for (int j = 0; j < names.size(); j++) {
          
          if (names.get(j).length() > whitespace) {
            
            whitespace = names.get(j).length();
          }
        }
        
        whitespace += 4;
        
        String spaces = "";
        
        while (whitespace > 0) { spaces += " "; whitespace--; }
        
        whitespace = spaces.length();
        /* SECTION WORKS TO HAVE CORRECT AMOUNT OF WHITESPACE BETWEEN NAME AND SEQUENCE */
        
        for (int j = 0; j < names.size(); j++) { pw.printf("%." + whitespace + "s%s\n", names.get(j)+spaces, seqs.get(j)); }
        
        pw.close();
      }
      
      /* CONVERSION PROCESS ENDS */
      /*---------------------------------------------------------------------------------------------------------------------------------------------------*/
      /* CONVERSION PROCESS ENDS */
    }
    /* Loop To Convert Files To NEXUS, PHYLIP, and FASTA */
    
  }
}