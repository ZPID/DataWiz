package de.zpid.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomResourceLoader {

  public void loadResourceData() throws IOException {

    try {
      final int NUM_CORES = Runtime.getRuntime().availableProcessors();
      ExecutorService exec = Executors.newFixedThreadPool(NUM_CORES * 2);
      for (int i = 0; i < 10; i++) {
        exec.submit(new Runnable() {
          @Override
          public void run() {
            try {
              System.out.println("Running ");
              File file = new File("C:\\Program Files\\IBM\\SPSS\\Statistics\\23\\spssjavaplugin.jar");
              URL url = file.toURI().toURL();
              URL[] urls = new URL[] { url };

              ClassLoader cl = new URLClassLoader(urls, this.getClass().getClassLoader());
              Class<?> statsUtil = cl.loadClass("com.ibm.statistics.plugin.StatsUtil");
              System.out.println(statsUtil.getName());
              Method method = statsUtil.getMethod("stop");
              method.invoke(statsUtil);
              method = statsUtil.getMethod("start");
              method.invoke(statsUtil);
              method = statsUtil.getMethod("submit", String.class);
              method.invoke(statsUtil, "GET FILE='C:\\Users\\ronny\\OneDrive\\ZPID\\Test.sav'.");
              Class<?> dataUtil = cl.loadClass("com.ibm.statistics.plugin.DataUtil");
              Constructor<?> c = dataUtil.getConstructor(); // we get the implicit constructor without parameters
              Object plugin = c.newInstance();
              method = dataUtil.getMethod("setConvertDateTypes", boolean.class);
              method.invoke(plugin, true);

              Class<?> cases = cl.loadClass("com.ibm.statistics.plugin.Case");
              method = dataUtil.getMethod("fetchCases", boolean.class, int.class);
              List<?> data = Arrays.asList(method.invoke(plugin, false, 0));

              System.out.println(data.size());

              // Class<?> myclass = mycl.loadClass("USAGE"); // get the class
              // Method m = myclass.getMethod("main", String[].class); // get the method you want to call
              // String[] args = new String[0]; // the arguments. Change this if you want to pass different args
              // m.invoke(null, args); // invoke the method
              method = statsUtil.getMethod("stop");
              method.invoke(statsUtil);
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        });
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }
}