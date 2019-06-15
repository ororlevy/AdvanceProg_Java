package test_server;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Random;

public class TestServer {
	

	public static void runClient(int port){
		Socket s=null;
		PrintWriter out=null;
		BufferedReader in=null;
		try{
			s=new Socket("127.0.0.1",port);
			s.setSoTimeout(5000);
			out=new PrintWriter(s.getOutputStream());
			in=new BufferedReader(new InputStreamReader(s.getInputStream()));
			/*
			Random r=new Random() ;
			int[][] matrix=new int[4][4];
			for(int i=0;i<matrix.length;i++)
				for(int j=0;j<matrix[i].length;j++)
					matrix[i][j]=100+r.nextInt(101);

			 */
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";
			int[][] mapData = new int[0][];
			ArrayList<String[]> numbers = new ArrayList<>();
			try {

				br = new BufferedReader(new FileReader(new File("map-Honolulu2.csv")));
				while ((line = br.readLine()) != null) {
					numbers.add(line.split(cvsSplitBy));
				}
				mapData = new int[numbers.size()][];
				for (int i = 0; i < numbers.size(); i++) {
					mapData[i] = new int[numbers.get(i).length];
					for (int j = 0; j < numbers.get(i).length; j++) {
						String tmp=numbers.get(i)[j];
						mapData[i][j] = Integer.parseInt(tmp);
					}
				}


			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
				/*
			StringBuilder sol=new StringBuilder();
			int i=0,j=0;
			while(i<3 || j<3){
				if(j<3 && r.nextBoolean()){
					sol.append(",Right");
					j++;
					matrix[i][j]=r.nextInt(100);
				}else{
					if(i<3){
						sol.append(",Down");
						i++;
						matrix[i][j]=r.nextInt(100);						
					}
				}
			}

				 */
				//String fsol=sol.substring(1);
			double min=Double.MAX_VALUE;
			double max=0;
			for(int i=0;i<mapData.length;i++)
				for (int j=0;j<mapData[i].length;j++)
				{
					if(min>mapData[i][j])
						min=mapData[i][j];
					if(max<mapData[i][j])
						max=mapData[i][j];
				}
			double new_max=255;
			double new_min=0;
			double avg=(max+min)/2;
			for (int i=0;i<mapData.length;i++)
				for (int j=0;j<mapData[i].length;j++)
				{
					mapData[i][j]=(int)((mapData[i][j]-min)/(max-min)*(new_max-new_min)+new_min);
					if(mapData[i][j]==0)
						mapData[i][j]=300;
					else
						mapData[i][j]=255-mapData[i][j];
				}

			int j,i;
				System.out.println("\tsending problem...");
				for (i = 0; i < mapData.length; i++) {
					System.out.print("\t");
					for (j = 0; j < mapData[i].length - 1; j++) {
						out.print(mapData[i][j] + ",");
						System.out.print(mapData[i][j] + ",");
					}
					out.println(mapData[i][j]);

					System.out.println(mapData[i][j]);
				}
				out.println("end");
				out.println("100,100");
				out.println("150,240");
				out.flush();
				System.out.println("\tend\n\t0,0\n\t3,3");
				System.out.println("\tproblem sent, waiting for solution...");
				String usol = null;
				try {
					usol = in.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("\tsolution received");
				System.out.println(usol);
			/*
			if(!usol.equals(fsol)){
				System.out.println("\twrong answer from your server (-20)");
			
				System.out.println("\t\tyour solution: "+usol);
				System.out.println("\t\texpected solution: "+fsol);
			}

			 */

		}catch(SocketTimeoutException e){
			System.out.println("\tYour Server takes over 3 seconds to answer (-20)");
		}catch(IOException e){
			System.out.println("\tYour Server ran into some IOException (-20)");
		}finally{
			try {
				in.close();
				out.close();
				s.close();
			} catch (IOException e) {
				System.out.println("\tYour Server ran into some IOException (-20)");
			}
		}
		
	}

}
