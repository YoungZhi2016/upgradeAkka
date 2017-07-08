package com.player.upgrade.backupReplace;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

import com.player.upgrade.utils.Configs;
import com.player.upgrade.utils.FileUtil;
import com.player.upgrade.utils.Logs;

public class UpgradePlayerImpl {

	/**
	 * player path
	 */
	private static Path aPlayerPath;

	/**
	 * backup path
	 */
	private static Path aBackupPath;

	/**
	 * update pack path
	 */
	private static Path aUpdatePackPath;

	private WatchIndex aWatchIndex;

	private UpgradePlayerImpl() {
		aPlayerPath = Configs.getPlayerDirPath();
		aBackupPath = Configs.getBackupDirPath();
		aUpdatePackPath = Configs.getUpdatePackUnDirPath();
		aWatchIndex = WatchIndex.getInstansce();
	}

	public static UpgradePlayerImpl getInstance() {
		return UpgradeHolder.aPlayer;
	}

	/**
	 * backup player file
	 */
	public boolean backuPlayer() {
		boolean ba = false;
		Path playerPath = aPlayerPath;
		Path backupPath = aBackupPath;
		// backup player file form configuration
		Set<String> backSet = Configs.getBackupDirFile();
		Set<Path> paths = new HashSet<>();
		if (backSet != null) {
			backSet.forEach(name -> paths.add(playerPath.resolve(name)));
		}

		Path dest;
		try {
			for (Path path : paths) {
				dest = backupPath.resolve(path.subpath(playerPath.getNameCount(), path.getNameCount()));
				if (Files.isDirectory(path)) {
					FileUtil.copyDirs(path, dest.getParent(), StandardCopyOption.REPLACE_EXISTING);
				} else {
					FileUtil.copyFile(path, dest);
				}
			}
			ba = true;
		} catch (IOException e) {
			Logs.error("backuPlayer error: " + e.getMessage());
		}
		return ba;
	}

	/**
	 * update replace player file
	 */
	public boolean updateReplace() {
		aWatchIndex.start();
		boolean u = false;
		// upgrade file path
		Path upgradePath = aUpdatePackPath;
		Path playerPath = aPlayerPath;
		try {
			FileUtil.copyDirs(upgradePath, playerPath, false, StandardCopyOption.REPLACE_EXISTING);
			u = true;
		} catch (IOException e) {
			Logs.error("updateReplace error: " + e.getMessage());
		}
		return u;
	}

	/**
	 * roll back replace file
	 */
	public void rollBackReplace() {
		Path backupPath = aBackupPath;
		Path playerPath = aPlayerPath;

		Set<Path> changeFilePath = aWatchIndex.getChangFilePath();
		try {
			rollBackReplace(backupPath, playerPath, changeFilePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Logs.error("rollBackReplace error: " + e.getMessage());
		}
	}

	private static void rollBackReplace(Path source, Path target, Set<Path> changeFilePath, CopyOption... options)
			throws IOException {
		if (null == source || !Files.isDirectory(source)) {
			// throw new IllegalArgumentException("source must be directory");
			Logs.error("source must be directory");
			return;
		}

		Path dest = target;
		// 如果相同则返回
		if (Files.exists(dest) && Files.isSameFile(source, dest))
			return;
		// 目标文件夹不能是源文件夹的子文件夹
		if (FileUtil.isSub(source, dest))
			throw new IllegalArgumentException("dest must not  be sub directory of source");

		Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				// create targetPath subDir
				Path subDir = 0 == dir.compareTo(source) ? dest
						: dest.resolve(dir.subpath(source.getNameCount(), dir.getNameCount()));
				Files.createDirectories(subDir);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (changeFilePath.contains(source.relativize(file))) {
					// System.err.println("rollBackReplace->file: " + file + " :
					// " + source.relativize(file));
					Files.copy(file, dest.resolve(file.subpath(source.getNameCount(), file.getNameCount())), options);
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * clean junk files
	 * 
	 * @param deletePackFile
	 *            true-->delete UpdatePack File
	 */
	public boolean cleanJunkFiles() {
		return cleanJunkFiles(null);
	}

	public boolean cleanJunkFiles(String packFilePath) {
		// clear playerUpdatePack
		boolean resp = FileUtil.deleteDir(aUpdatePackPath);

		// clear backup file
		boolean resb = FileUtil.deleteDir(aBackupPath);

		// clear update pack file
		if (packFilePath != null) {
			Path packPath = Paths.get(packFilePath);
			if (Files.exists(packPath)) {
				int number = 0;
				boolean b = false;
				b = deletePack(b, packPath);
				while (!b) {
					b = deletePack(b, packPath);
					if (number > 5) {
						b = true;
					}
					number++;
				}
			}
		}
		aWatchIndex.close();
		return resb && resp;
	}

	private boolean deletePack(boolean b, Path packPath) {
		System.gc();
		b = FileUtil.deleteDir(packPath);
		return b;
	}

	private static final class UpgradeHolder {
		private static UpgradePlayerImpl aPlayer = new UpgradePlayerImpl();
	}

}