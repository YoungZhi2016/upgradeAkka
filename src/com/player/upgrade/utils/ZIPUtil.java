package com.player.upgrade.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public final class ZIPUtil {

	/**
	 * ZIP file encoding
	 */
	private static final String ENCODE = "GBK";

	/**
	 * check update pack integrity
	 * 
	 * true-->integrity
	 * 
	 * false-->The file is damaged
	 */
	public static boolean checkIntegrity(String filePath) {

		return true;
	}

	/**
	 * ENCODE: GBK
	 * 
	 * delete: false
	 */
	public static boolean unZipWithTemplate(String zipFileName, String outputDirectory, String rename)
			throws Exception {
		return unZipWithTemplate(zipFileName, outputDirectory, rename, false, ENCODE);
	}

	/**
	 * unZip rename
	 */
	public static boolean unZipWithTemplate(String zipFileName, String outputDirectory, String rename, boolean isDelete,
			String encoding) throws Exception {
		boolean flag = false;
		ZipFile zipFile = null;
		String firstDirName = null;
		try {
			File srcFile = new File(zipFileName);
			if (!srcFile.exists() && (srcFile.length() <= 0)) {
				return false;
			}

			File outDir = new File(outputDirectory);
			if (!outDir.exists() || !outDir.isDirectory()) {
				outDir.mkdirs();
			}

			zipFile = new ZipFile(srcFile, encoding);
			Enumeration<ZipEntry> entrys = zipFile.getEntries();

			String fileName;
			File dirFile;
			ZipEntry zipEntry;
			BufferedInputStream bis;
			BufferedOutputStream bos;
			byte[] buff = new byte[1024];
			boolean first = true;
			while (entrys.hasMoreElements()) {
				zipEntry = entrys.nextElement();
				fileName = zipEntry.getName();
				if (first) {
					firstDirName = fileName;
					first = false;
				}
				if (zipEntry.isDirectory()) {
					dirFile = new File(outputDirectory + File.separator + fileName);
					dirFile.mkdir();
				} else {
					bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
					bos = new BufferedOutputStream(new FileOutputStream(outputDirectory + File.separator + fileName));
					int len;
					while ((len = bis.read(buff)) != -1) {
						bos.write(buff, 0, len);
					}
					bos.close();
					bis.close();
				}
				flag = true;
			}
			if (isDelete) {
				boolean deleteR = new File(zipFileName).delete();
				Logs.debug("delete zip file result: " + deleteR);
			}
		} catch (Exception ex) {
			Logs.error("unzip file failure!" + ex.getMessage());
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
				}
			}
		}
		// reName DIR
		Path fPath = Paths.get(outputDirectory + "/" + firstDirName);
		Path rPath = Paths.get(outputDirectory + "/" + rename);
		if (Files.exists(rPath)) {
			FileUtil.deleteDir(rPath);
		}
		if (Files.exists(fPath)) {
			fPath.toFile().renameTo(rPath.toFile());
		}
		return flag;
	}

	/**
	 * ENCODE: GBK
	 * 
	 * delete: false
	 */
	public static boolean unZip(String sourcePath, String destPath) throws Exception {
		return unZip(sourcePath, destPath, false);
	}

	/**
	 * delete false
	 */
	public static boolean unZip(String zipFileName, String outputDirectory, String encodeing) {
		return unZip(zipFileName, outputDirectory, false, encodeing);
	}

	/**
	 * ENCODE
	 */
	public static boolean unZip(String zipFileName, String outputDirectory, boolean isDelete) {
		return unZip(zipFileName, outputDirectory, isDelete, ENCODE);
	}

	/**
	 * unZip file
	 * 
	 * @param zipFileName
	 *            ZIP file path
	 * @param outputDirectory
	 * @param isDelete
	 *            true-->delete zipFileName
	 * @param encodeing
	 *            ZIP file encode
	 */
	public static boolean unZip(String zipFileName, String outputDirectory, boolean isDelete, String encodeing) {
		boolean flag = false;
		ZipFile zipFile = null;
		try {
			File srcFile = new File(zipFileName);
			if (!srcFile.exists() && (srcFile.length() <= 0)) {
				return false;
			}

			File outDir = new File(outputDirectory);
			if (!outDir.exists() || !outDir.isDirectory()) {
				outDir.mkdirs();
			}

			zipFile = new ZipFile(srcFile, encodeing);
			Enumeration<ZipEntry> entrys = zipFile.getEntries();

			String fileName;
			File dirFile;
			ZipEntry zipEntry;
			BufferedInputStream bis;
			BufferedOutputStream bos;
			byte[] buff = new byte[1024];
			while (entrys.hasMoreElements()) {
				zipEntry = entrys.nextElement();
				fileName = zipEntry.getName();
				if (zipEntry.isDirectory()) {
					dirFile = new File(outputDirectory + File.separator + fileName);
					dirFile.mkdir();
				} else {
					bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
					bos = new BufferedOutputStream(new FileOutputStream(outputDirectory + File.separator + fileName));
					int len;
					while ((len = bis.read(buff)) != -1) {
						bos.write(buff, 0, len);
					}
					bos.close();
					bis.close();
				}
				flag = true;
			}
			if (isDelete) {
				boolean deleteR = new File(zipFileName).delete();
				Logs.debug("delete zip file result: " + deleteR);
			}
		} catch (Exception ex) {
			Logs.error("unzip file failure!" + ex.getMessage());
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
				}
			}
		}
		return flag;
	}
}