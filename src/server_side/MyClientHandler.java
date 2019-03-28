package server_side;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MyClientHandler implements ClientHandler {
	CacheManager cm;
	Solver solver;
	public MyClientHandler(CacheManager cm ) {
		this.cm=cm;
	}
	
	@Override
	public void handleClient(InputStream in, OutputStream out) {
		BufferedReader Bin=new BufferedReader(new InputStreamReader(in));
		PrintWriter Bout=new PrintWriter(new OutputStreamWriter(out));
			try {
				String Line;
				String Solved;
				ArrayList<String[]> lines=new ArrayList<String[]>();
				
				while(!(Line= Bin.readLine()).equals("end"))
				{
					lines.add(Line.split(","));
				}
				int j=0;
				int[][]mat=new int[lines.size()][];
				for (int i = 0; i < mat.length; i++) {
					String[] tmp=lines.get(i);
					mat[i]=new int[tmp.length];
					for (String s : tmp) {
						mat[i][j]=Integer.parseInt(s);
						j++;
					}
					j=0;
				}
			Matrix m=new Matrix(mat);
			Searcher searcher=new BFS();
			solver=new SolverSearcher<>(searcher);
			m.setIntialState(Line= Bin.readLine());
			m.setGoalState(Line= Bin.readLine());
			if(cm.Check(m.toString()))
			{
				Solved=(String) cm.Extract(m.toString());
			}
			else {
				Solved=(String) solver.Solve(m);
				String[] arrows=Solved.split("->");
				Solved="";
				String[] arrow1;
				String[] arrow2;
				int x,y;
				for (int i = 0; i < arrows.length-1; i++) {
					arrow1=arrows[i].split(",");
					arrow2=arrows[i+1].split(",");
					x=Integer.parseInt(arrow2[0])-Integer.parseInt(arrow1[0]);
					y=Integer.parseInt(arrow2[1])-Integer.parseInt(arrow1[1]);
					if(x>0)
						Solved+="Down"+",";
					else if(x<0)
						Solved+="Up"+",";
					else
						if(y>0)
							Solved+="Right"+",";
						else
							Solved+="Left"+",";
				
						}
				cm.Save(m.toString(), Solved);
			}
			Bout.println(Solved.substring(0, Solved.length()-1));
			Bout.flush();
		}catch (IOException e) {e.printStackTrace();}
		
		try {
			Bin.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bout.close();

	}

}
