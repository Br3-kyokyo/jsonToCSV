import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class main {

	private static PrintWriter pw;
	private static Iterator<String> fieldNames_g;
	private static Iterator<String> fieldNames_e;
	private static JsonNode root_a;
	private static JsonNode root_g;
	private static JsonNode root_e;
	private static HashMap<String, Integer> id_row;
	private static BufferedWriter bw;
	private static File file;


	public static void main(String[] args) throws JsonProcessingException, IOException{

		String csv_location = ".";
		String jsn_location = ".";
/*
		try{
			csv_location = args[0];
			jsn_location = args[1];
		}catch(ArrayIndexOutOfBoundsException e){

			System.out.println("第一引数:CSVを出力したいディレクトリのパス ex) ../home/Desktop  ");
			System.out.println("第二引数:jsonファイルの存在するディレクトリのパス ex) ../deta/sohosai17  ");
			System.out.println("を指定してください");

			System.exit(0);

		}
*/

		System.out.println("Start");

		id_row = new HashMap<String,Integer>();	//団体IDと列が入っている

		file = new File(csv_location + "/temp.csv"); //ファイルを作成

		if(file.exists()){		//既にファイルがあれば再生成する。
			file.delete();
			file.createNewFile();
		}else{
			file.createNewFile();
		}

		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"Shift_JIS"));
		pw = new PrintWriter(bw);

		ObjectMapper mapper = new ObjectMapper();
		root_a = mapper.readTree(new File(jsn_location + "/account.json"));
		root_g = mapper.readTree(new File(jsn_location + "/group.json"));
		root_e = mapper.readTree(new File(jsn_location + "/event.json"));

	    root_a.fieldNames();

		//group.jsonを書き出し

		group();			//企画団体名
		group2("kana");	//かな
		//group1();


		//account.jsonを書き出し

		//pw.println("以下、責任者・副責任者情報");

		//アカウント情報を書き出し

		account("rep");//責任者情報を書き出し
		account("member");//副責を書き出し

		//申請項目を書き出し
		//pw.println("以下、申請情報");

		event();

		pw.close();


		new Q_code(csv_location + "/temp.csv",csv_location + "/data.csv");
	}



private static void group1() throws IOException {

	pw.print("団体ID,");

	fieldNames_g = root_g.fieldNames();

	while(fieldNames_g.hasNext()){
		//System.out.println(fieldNames_g.next());
		try{
			pw.print(fieldNames_g.next()+",");
		}catch(NoSuchElementException e){}
	}
	bw.newLine();
}


	private static void event() throws IOException {

		fieldNames_e = root_e.get("form").fieldNames();


		//凡例はfieldNamesに格納

		while(fieldNames_e.hasNext()){


			String item = fieldNames_e.next();
			String type = null;


			try{
				type = root_e.get("form").get(item).get("type").toString();
			}catch(NullPointerException e){

			}

			try{
				pw.print(root_e.get("form").get(item).get("name").toString()+","); //項目名を出力

			}catch(NullPointerException e){
				continue;
			}



			int hoge1 = 1;
		    fieldNames_g = root_g.fieldNames();


		    String group_id;




			while(fieldNames_g.hasNext()){

				group_id = fieldNames_g.next();	//団体IDを走査


				while(id_row.get(group_id) > hoge1){pw.print(","); hoge1++;}	//列をそろえる

				if(type.equals("\"select\"")){


					try{
						String ans_id = root_e.get("form").get(item).get("ans").get(group_id).asText();

						String ans_str = root_e.get("form").get(item).get("select").get(ans_id).toString();
						ans_str = replace_str(ans_str,",","、");
						ans_str = replace_str(ans_str,"&amp;","&");

						pw.print(ans_str+",");	//回答内容を出力
					}catch(NullPointerException e){
						pw.print(",");
					}

				}if(type.equals("\"multiple\"")){


					try{
						String ans_id = root_e.get("form").get(item).get("ans").get(group_id).asText();


						String[] ans_str_array = ans_id.split("\\/");

						Iterator<String> itr = Arrays.asList(ans_str_array).iterator();

						while(itr.hasNext()){

							String temp = itr.next();

							//System.out.println(temp);

							String ans_str = root_e.get("form").get(item).get("select").get(temp).toString();
							ans_str = replace_str(ans_str,",","、");
							ans_str = replace_str(ans_str,"&amp;","&");

							//System.out.println(ans_str);

							pw.print(ans_str + " ");	//回答内容を出力
						}

						pw.print(",");

					}catch(NullPointerException e){
						pw.print(",");
					}




				}if(type.equals("\"text\"") || type.equals("\"textarea\"")){

						try{
							String ans_str = root_e.get("form").get(item).get("ans").get(group_id).toString();
							ans_str = replace_str(ans_str,",","、");
							ans_str = replace_str(ans_str,"&amp;","&");
							ans_str = replace_str(ans_str,"\r\n","改行");
							System.out.println(ans_str);

							//ans_str = ans_str.replaceAll(System.lineSeparator(), " ");


							System.out.print(ans_str);
							pw.print(ans_str+",");	//回答内容を出力
						}catch(NullPointerException e){
							pw.print(",");
						}

				}if(type.equals("\"order\"")){

					Iterator<String> fieldName_odr = null;

					try{
						fieldName_odr = root_e.get("form").get(item).get("ans").get(group_id).fieldNames();

						//各項目に関する解答をIteratorに代入
						while(fieldName_odr.hasNext()){		//各団体が申請した物品のIDを列挙

							String odr_ans = fieldName_odr.next();

							try{
								String target = root_e.get("form").get(item).get("select").get(odr_ans).toString();//物品の名前を出力
								String ans_str = root_e.get("form").get(item).get("ans").get(group_id).get(odr_ans).toString();	//物品の数量を出力
								ans_str = replace_str(ans_str,",","、");
								ans_str = replace_str(ans_str,"&amp;","&");
								pw.print(target+":"+ans_str+" ");	//回答内容を出力
							}catch(NullPointerException e){}
						}

						pw.print(",");

					}catch(NullPointerException e){pw.print(",");}

				}else{}

				hoge1++;
			}

			bw.newLine();

		}



	}





	private static void account(String string) throws IOException {

		String syurui = string;

	    account_sub("name",syurui);
	    account_sub("kana",syurui);
	    account_sub("email",syurui);
	    account_sub("phone",syurui);
	    account_sub("id",syurui);
	    account_sub("college",syurui);

	}



	private static void account_sub(String string,String syurui) throws IOException {

	    root_a.fieldNames();
	    fieldNames_g = root_g.fieldNames();


	    int hoge1 = 1;


	    output_hanrei(string,syurui);

		while(fieldNames_g.hasNext()){

			String group_id = null;
			try{
				group_id = fieldNames_g.next();
			}catch(NoSuchElementException e){
				break;
			}
			//企画団体IDを取得

			while(id_row.get(group_id) >= hoge1){pw.print(","); hoge1++;}
			//団体IDと列を一致


			switch(syurui){

				case "rep" :

					try{		//色々出力

						String temp = root_a.get(root_g.get(group_id).get(syurui).toString()).get(string).toString();
						temp = replace_str(temp,",","、");
						pw.print(temp+",");

					}catch(NullPointerException e){
						pw.print(",");
					}

					break;

				case "member":

					int i = 0;
					int member_size = Integer.MIN_VALUE;

					try{
						member_size = root_g.get(group_id).get(syurui).size();
					}catch(NullPointerException e){}

					while(member_size > i){

							try{		//色々出力

								String temp = root_a.get(root_g.get(group_id).get(syurui).get(i).toString()).get(string).toString();
								temp = replace_str(temp,",","、");
								pw.print(temp+" ");

							}catch(NullPointerException e){}

							i++;
					}

					pw.print(",");

					break;

			}




			hoge1++;
		}
		bw.newLine();

	}



	private static void output_hanrei(String string, String syurui) {

		switch(syurui){
		case "rep":
				pw.print("責任者の"+string);
				break;
		case "member":
				pw.print("副責の"+string);
				break;
		}
	}



	private static void group2(String string) throws IOException {

		pw.print("企画団体名(カナ),");

	    int hoge1 = 1;
	    fieldNames_g = root_g.fieldNames();

		while(fieldNames_g.hasNext()){

			String group_id = null;

			try{
				group_id = fieldNames_g.next();
			}catch(NoSuchElementException e){
				break;
			}

			while(id_row.get(group_id) > hoge1){pw.print(","); hoge1++;}

			try{
				String temp = root_g.get(group_id).get(string).toString();
				temp = replace_str(temp,",","、");
				pw.print(temp+",");	//団体名を出力
			}catch(NullPointerException e){}

			hoge1++;
		}
		bw.newLine();

	}



	private static void group() throws IOException{

	    pw.print("企画団体名,");

	    int hoge1 = 1;
	    fieldNames_g = root_g.fieldNames();
		while(fieldNames_g.hasNext()){

			String temp = null;

			try{
				temp = fieldNames_g.next();
			}catch(NoSuchElementException e){
				break;
			}

			String temp1 =root_g.get(temp).get("name").toString();
			temp1=replace_str(temp1,",","、");

			pw.print(temp1+",");	//団体名を出力

			id_row.put(temp, hoge1); //団体IDと列を関連付け

			hoge1++;
		}
		bw.newLine();
	}



	private static String replace_str(String target_str, String before_splitting_char,String after_splitting_char) {

		Pattern p = Pattern.compile(before_splitting_char);
		Matcher m = p.matcher(target_str);
		String result = m.replaceAll(after_splitting_char);

		return result;
	}

}

