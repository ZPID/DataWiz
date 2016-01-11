package de.zpid.datawiz.util;

import java.awt.image.BufferedImage;
import java.security.MessageDigest;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

public class FileUtil {

  /**
   * 
   * @param digest
   * @param hash
   * @return
   */
  public static String getFileChecksum(MessageDigest digest, final byte[] hash) {
    return (new HexBinaryAdapter()).marshal(digest.digest(hash)).toLowerCase();
  }

  /**
   * 
   * @param src
   * @param w
   * @param h
   * @return
   */
  public static BufferedImage scaleImage(BufferedImage src, int w, int h) {
    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    int x, y;
    int ww = src.getWidth();
    int hh = src.getHeight();
    for (x = 0; x < w; x++) {
      for (y = 0; y < h; y++) {
        int col = src.getRGB(x * ww / w, y * hh / h);
        img.setRGB(x, y, col);
      }
    }
    return img;
  }
}
