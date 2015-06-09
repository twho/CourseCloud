package com.coursecloud.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.coursecloud.constants.FileIOConstants;

import android.content.Context;
import android.util.Log;

public class FileIOForTeam implements FileIOConstants {
	Context _context;
	private String TAG = "FileIOForTeam_DEBUG";

	public FileIOForTeam(Context context) {
		_context = context;

	}

	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String, Object>> getTeamList() {
		File file = new File(_context.getFilesDir(), TEAMLIST);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return (ArrayList<HashMap<String, Object>>) readObject(file);
	}
	public String getTeamListDir() {
		File dir = new File(_context.getFilesDir() + File.separator + TEAM_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir.getPath();
	}

	public Boolean checkTeamExist(String teamName) {

		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		list = getTeamList();
		Log.w(TAG, "checkContactExist = " + list);

		// Read MESSAGE only
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).get(TEAMNAME).equals(teamName)) {
					Log.d(TAG, i + ". found exised : " + list.get(i));
					return true;
				}
			}
		}
		return false;
	}
	public void addTeam(HashMap<String, Object> item) {
		File file = new File(_context.getFilesDir(), TEAMLIST);
		ArrayList<HashMap<String, Object>> list;

		if (getTeamList() == null) {
			list = new ArrayList<HashMap<String, Object>>();
		} else {
			list = getTeamList();
		}
		list.add(item);
		writeObject(file, list);
	}
	public void delTeamDetail() {
		File file = new File(_context.getFilesDir(), TEAMLIST);
		if (file.exists()) {
			try {
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	private Object readObject(File file) {
		Object returnlist = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			returnlist = ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnlist;
	}
	private boolean writeObject(File file, Object object) {

		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public void writeFileToList(String filename, String string) {

		writeFile(getTeamListDir(), filename, string);
	}

	public void writeFile(String dir, String filename, String string) {
		FileWriter fw;
		try {
			File f = new File(dir, filename);
			if (!f.exists()) {
				f.createNewFile();
			}
			fw = new FileWriter(f, true);
			fw.append(string);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
