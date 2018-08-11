package repo;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

class Compare {
    //This can be any folder locations which you want to compare
    private static List<File> newRepos = new ArrayList<File>();
    static File dir1 = new File("D:\\git_tests\\old\\box-legacy");
    static File dir2 = new File("D:\\git_tests\\new\\box-legacy");
    public static void main(String ...args)
    {
        Compare compare = new Compare();
        copyRepos(dir1, dir2);
        try
        {
            compare.getDiff(newRepos.get(1),newRepos.get(2));
        }
        catch(IOException ie)
        {
            ie.printStackTrace();
        }
        deleteNewRepos(newRepos);
    }

    private static void deleteNewRepos(List<File> newRepos) {
        for (File repo : newRepos){
            try {
                FileUtils.deleteDirectory(repo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void copyRepos(File dir1, File dir2) {
        String destination = "D:\\git_tests\\new_repos1";
        String destination2 = "D:\\git_tests\\new_repos1";
        File destDir = new File(destination);
        File destDir2 = new File(destination2);

        try {
            FileUtils.copyDirectory(dir1, destDir);
            newRepos.add(destDir);
            FileUtils.copyDirectory(dir2, destDir2);
            newRepos.add(destDir2);
        } catch (IOException e) {
            e.printStackTrace();
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
            map1 = new HashMap<String, File>();
            for(int i=0;i<fileList1.length;i++)
            {
                map1.put(fileList1[i].getName(),fileList1[i]);
            }

            compareNow(fileList2, map1);
        }
        else
        {
            map1 = new HashMap<String, File>();
            for(int i=0;i<fileList2.length;i++)
            {
                map1.put(fileList2[i].getName(),fileList2[i]);
            }
            compareNow(fileList1, map1);
        }
    }

    public void compareNow(File[] fileArr, HashMap<String, File> map) throws IOException
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
                        System.out.println(aFileArr.getAbsolutePath() + "\t\t" + "different");
                    }
                }
            } else {
                if (aFileArr.isDirectory()) {
                    traverseDirectory(aFileArr);
                } else {
                    System.out.println(aFileArr.getName() + "\t\t" + "only in " + aFileArr.getParent());
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
                System.out.println(fileFrmMap.getName() + "\t\t" + "only in " + fileFrmMap.getParent());
            }
        }
    }

    public void traverseDirectory(File dir)
    {
        File[] list = dir.listFiles();
        for(int k=0;k<list.length;k++)
        {
            if(list[k].isDirectory())
            {
                traverseDirectory(list[k]);
            }
            else
            {
                System.out.println(list[k].getName() +"\t\t"+"only in "+ list[k].getParent());
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
        catch (Exception e)
        {
            return null;
        }
        finally {
            if (fin != null){
                fin.close();
            }

        }
    }
}
