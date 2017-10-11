
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

public class Q_code {

	int num_cols;// ファイルの列の要素の数
	int num_rows;// ファイルの行の要素の数
	List<String> list_file = new LinkedList<String>();// ファイルの行のデータを詰めたリスト

	String[][] data;// ２次元配列

	/**
	 * ファイルを読み込んで行をString型でlist_fileに詰め込む
	 *
	 * @param path
	 *            読み込むファイルのパス
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void read_file(String path) throws IOException {

		InputStreamReader filereader = new InputStreamReader(new FileInputStream(new File(path)),"Shift_JIS");

		BufferedReader br = new BufferedReader(filereader);

		String str;

		while ((str = br.readLine()) != null) {
			list_file.add(str);
		}

		br.close();
	}

	/**
	 * リストからStringの2次元配列に変換する
	 *
	 * @throws IOException
	 */
	private void CreateMat() {
		num_rows = list_file.size();
		String[] sv_buf = list_file.get(0).split(",", -1);
		num_cols = sv_buf.length;

		data = new String[num_rows][num_cols];

		for (int i = 0; i < num_rows; i++) {
			sv_buf = list_file.get(i).split(",", -1);

			for (int j = 0; j < num_cols; j++) {
					try{
					data[i][j] = sv_buf[j];
				}catch(ArrayIndexOutOfBoundsException e){
					continue;
				}
			}
		}
	}

	/**
	 * ファイルに転置したものを書き出す
	 *
	 * @param path
	 * @throws IOException
	 */
	private void Write(String path) throws IOException {
		File file = new File(path);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"Shift_JIS"));

		for (int i = 0; i < num_cols; i++) {
			for (int j = 0; j < num_rows; j++) {
				if(data[j][i]!=null){
					bw.write(data[j][i] + ",");
				}else{
					bw.write(",");
				}
			}
			bw.newLine();
		}

		bw.close();
	}

	Q_code(String src, String dst ) throws IOException {
		read_file(src);// !適宜変更
		Row_deform(); //行の入れ替え
		CreateMat();
		Write(dst);// !適宜変更
	}

	private void Row_deform() {
		list_file.add(2, list_file.get(26/* - 6*/));
		list_file.remove(27/* - 6*/);

		list_file.add(3, list_file.get(27/* - 6*/));
		list_file.remove(28/* - 6*/);

		list_file.add(4, list_file.get(40/* - 6*/));
		list_file.remove(41/* - 6*/);

		list_file.add(0, list_file.get(77/* - */));
		list_file.remove(78/*- 6*/);


	}

}
