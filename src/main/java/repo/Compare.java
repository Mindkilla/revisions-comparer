package repo;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

public class Compare {
    public static final Logger logger = Logger.getLogger(Compare.class);
    //This can be any folder locations which you want to compare
    private static List<File> newRepos = new ArrayList<>();
    public static StringBuilder diffs = new StringBuilder();
    private static File dir1 = new File("D:\\git_tests\\old\\box-legacy");
    private static File dir2 = new File("D:\\git_tests\\new\\box-legacy");

    public String getDiffs(){
        return diffs.toString().replaceAll("pom.xml\t\tdifferent\r\n", "");
    }

    public void clearDiffs(){
        diffs.setLength(0);
    }

    public void doCompare()
    {
        copyRepos();
        try
        {
            getDiff(newRepos.get(0),newRepos.get(1));
        }
        catch(IOException ie)
        {
            logger.error(ie.getMessage());
        }
        deleteNewRepos(newRepos);
    }

    public void deleteNewRepos(List<File> newRepos) {
        for (File repo : newRepos){
            try {
                FileUtils.deleteDirectory(repo);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        newRepos.clear();
    }

    public void copyRepos() {
        File destDir = new File("D:\\git_tests\\old_repo");
        File destDir2 = new File("D:\\git_tests\\new_repo");

        try {
            FileUtils.copyDirectory(dir1, destDir);
            File destDirGit = new File("D:\\git_tests\\old_repo\\.git");
            FileUtils.deleteDirectory(destDirGit);
            newRepos.add(destDir);

            FileUtils.copyDirectory(dir2, destDir2);
            File destDir2Git = new File("D:\\git_tests\\new_repo\\.git");
            FileUtils.deleteDirectory(destDir2Git);
            newRepos.add(destDir2);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void getDiff(File dirA, File dirB) throws IOException
    {
        File[] fileList1 = dirA.listFiles();
        File[] fileList2 = dirB.listFiles();
        Arrays.sort(fileList1);
        Arrays.sort(fileList2);
        HashMap<String, File> map1;
        if(fileList1.length < fileList2.length)
        {
            map1 = new HashMap<>();
            for (File aFileList1 : fileList1) {
                map1.put(aFileList1.getName(), aFileList1);
            }

            compareNow(fileList2, map1);
        }
        else
        {
            map1 = new HashMap<>();
            for (File aFileList2 : fileList2) {
                map1.put(aFileList2.getName(), aFileList2);
            }
            compareNow(fileList1, map1);
        }
    }

    public void compareNow(File[] fileArr, Map<String, File> map) throws IOException
    {
        for (File aFileArr : fileArr) {
            String fName = aFileArr.getName();
            File fComp = map.get(fName);
            map.remove(fName);
            if (fComp != null) {
                if (fComp.isDirectory()) {
                    getDiff(aFileArr, fComp);
                } else {
                    String cSum1 = checksum(aFileArr);
                    String cSum2 = checksum(fComp);
                    if (!cSum1.equals(cSum2)) {
                        diffs.append(aFileArr.getName()).append("\t\t").append("different").append("\r\n");
                    }
                }
            } else {
                if (aFileArr.isDirectory()) {
                    traverseDirectory(aFileArr);
                } else {
                    diffs.append(aFileArr.getName()).append("\t\t").append("only in ").append(aFileArr.getParent()).append("\r\n");
                }
            }
        }
        Set<String> set = map.keySet();
        for (String n : set) {
            File fileFrmMap = map.get(n);
            map.remove(n);
            if (fileFrmMap.isDirectory()) {
                traverseDirectory(fileFrmMap);
            } else {
                diffs.append(fileFrmMap.getName()).append("\t\t").append("only in ").append(fileFrmMap.getParent()).append("\r\n");
            }
        }
    }

    public void traverseDirectory(File dir)
    {
        File[] list = dir.listFiles();
        for (File aList : list) {
            if (aList.isDirectory()) {
                traverseDirectory(aList);
            } else {
                diffs.append(aList.getName()).append("\t\t").append("only in ").append(aList.getParent()).append("\r\n");
            }
        }
    }

    public String checksum(File file) throws IOException {
        InputStream fin = null;
        try
        {
            fin = new FileInputStream(file);
            java.security.MessageDigest md5er = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int read;
            do
            {
                read = fin.read(buffer);
                if (read > 0)
                    md5er.update(buffer, 0, read);
            } while (read != -1);
            fin.close();
            byte[] digest = md5er.digest();
            if (digest == null)
                return null;
            StringBuilder strDigest = new StringBuilder("0x");
            for (byte aDigest : digest) {
                strDigest.append(Integer.toString((aDigest & 0xff) + 0x100, 16).substring(1).toUpperCase());
            }
            return strDigest.toString();
        }
        catch (Exception e) {
            return null;
        } finally {
            if (fin != null){
                fin.close();
            }
        }
    }
}
