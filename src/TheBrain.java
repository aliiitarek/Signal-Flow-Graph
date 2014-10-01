import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class TheBrain {
	private LinkedList<Integer>[] SFG;
	private int[][] AdjMatrix;
	private ArrayList< LinkedList<Integer> > forwardPaths;
	private ArrayList< LinkedList<Integer> > loops;
	private int[] forwardGains;
	private int[] loopGains;
	private LinkedList< LinkedList<Integer> >[] nonTouching;
	private int[] deltas;
	private int TotalGain;
	private int startNode,endNode;
	
	
	public TheBrain(int[][] Graph,int sn,int en){
		SFG = new LinkedList[Graph.length];
		AdjMatrix = Graph;
		forwardPaths = new ArrayList<LinkedList<Integer>>();
		loops = new ArrayList<LinkedList<Integer>>();
		for(int i=0;i<SFG.length;i++){
			SFG[i] = new LinkedList<>();
		}
		startNode = sn;
		endNode = en;
	}
	
	
	
	public void extractForwardPaths(int startNode, int endNode, int nowNode, boolean[] visited, LinkedList<Integer> path){
		visited[nowNode] = true;
		path.add(nowNode);
		if(nowNode==endNode){
			forwardPaths.add(path);
			return;
		}
		for(int d=0;d<SFG[nowNode].size();d++){
			if(!visited[SFG[nowNode].get(d)]){
				LinkedList<Integer> temp = new LinkedList<Integer>();
				for (int j = 0; j < path.size(); j++) {
					temp.add(path.get(j));
				}
				boolean[] tempb = new boolean[visited.length];
				for (int j = 0; j < visited.length; j++) {
					tempb[j] = visited[j];
				}
				extractForwardPaths(startNode, endNode, SFG[nowNode].get(d), tempb, temp);
			}
		}
	}
	
	public void recurrsionLoops(int startNode, int nowNode, boolean[] visited, LinkedList<Integer> loop){
		loop.add(nowNode);
		visited[nowNode] = true;
		for(int d=0;d<SFG[nowNode].size();d++){
			if(SFG[nowNode].get(d)==startNode && visited[SFG[nowNode].get(d)]){
				loop.add(SFG[nowNode].get(d));
				loops.add(loop);
				return;
			}
			if(!visited[SFG[nowNode].get(d)]){
				LinkedList<Integer> temp = new LinkedList<Integer>();
				for (int j = 0; j < loop.size(); j++) {
					temp.add(loop.get(j));
				}
				boolean[] tempb = new boolean[visited.length];
				for (int j = 0; j < visited.length; j++) {
					tempb[j] = visited[j];
				}
				recurrsionLoops(startNode, SFG[nowNode].get(d), tempb, temp);
			}
		}
	}
	
	public void extractLoops(){
		for(int i=0;i<SFG.length;i++){
			boolean s[] = new boolean[9];
			for(int j=0;j<i;j++){
				s[j] = true;
			}
			LinkedList<Integer> ln = new LinkedList<Integer>();
			recurrsionLoops(i, i, s, ln);
		}
	}
	
	public void printLoops(){
		System.out.println("Loops :");
		for (int i = 0; i < loops.size(); i++) {
			for (int j = 0; j < loops.get(i).size(); j++) {
				System.out.print(loops.get(i).get(j)+" ");
			}System.out.println(" Gain = "+loopGains[i]);
		}
	}
	
	public void printForwardPaths(){
		System.out.println("ForwardPaths :");
		for (int i = 0; i < forwardPaths.size(); i++) {
			for (int j = 0; j < forwardPaths.get(i).size(); j++) {
				System.out.print(forwardPaths.get(i).get(j)+" ");
			}System.out.println(" Gain = "+forwardGains[i]);
		}
	}
	
	public void getForwardGains(){
		forwardGains = new int[forwardPaths.size()];
		for(int i=0;i<forwardPaths.size();i++){
			int gain = 1;
			for(int j=0;j<(forwardPaths.get(i).size()-1);j++){
				gain*=AdjMatrix[forwardPaths.get(i).get(j)][forwardPaths.get(i).get(j+1)];
			}
			forwardGains[i] = gain;
		}
	}
	
	public void getLoopGains(){
		loopGains = new int[loops.size()];
		for(int i=0;i<loops.size();i++){
			int gain = 1;
			for(int j=0;j<(loops.get(i).size()-1);j++){
				gain*=AdjMatrix[loops.get(i).get(j)][loops.get(i).get(j+1)];
			}
			loopGains[i] = gain;
		}
	}
	
	public int getNonTouchedLoops(LinkedList<Integer> forbidden){
		int delta = 1;
		
		nonTouching = new LinkedList[loops.size()+1]; /*this is one-based and (index i means i-nontouching loops)*/
		/*initialize the nontouching datastructure*/
		for (int i = 0; i < nonTouching.length; i++) { 
			nonTouching[i] = new LinkedList<>();
		}
		int subDelta = 0;
		for (int i = 0; i < loops.size(); i++) {
			if(areTheyNonTouching(forbidden, loops.get(i))){
				LinkedList<Integer> dump = new LinkedList<>();
				dump.add(i);
				nonTouching[1].add(dump);
				subDelta+= loopGains[i];
			}
		}
		
		delta-=subDelta;
		subDelta = 1;
		if(loops.size()>=2){
			//make the 2-nonTouching Loops
			for (int i = 0; i <nonTouching[1].size(); i++) {
				for (int j = i+1; j <nonTouching[1].size(); j++) {
					if(areTheyNonTouching(loops.get(nonTouching[1].get(i).get(0)), loops.get(nonTouching[1].get(j).get(0)))){
						LinkedList<Integer> dump = new LinkedList<Integer>();
						dump.add(i);
						dump.add(j);
						nonTouching[2].add(dump);
						subDelta+=(loopGains[i]*loopGains[j]);
					}
				}
			}

			delta+=subDelta;
		}
		
		for(int i=3;i<nonTouching.length;i++){
			for(int j=0;j<nonTouching[i-1].size();j++){
				subDelta = 1;
				for(int k=0;k<nonTouching[1].size();k++){
					LinkedList<Integer> numOne = new LinkedList<Integer>();
					LinkedList<Integer> dummy = nonTouching[i-1].get(j);
					for(int l=0;l<dummy.size();l++){
						for(int p=0;p<loops.get(dummy.get(l)).size();p++){
							numOne.add(loops.get(dummy.get(l)).get(p));
						}
					}
					LinkedList<Integer> numTwo = new LinkedList<Integer>();
					LinkedList<Integer> stub1 = new LinkedList<>();
					stub1 = nonTouching[1].get(k);
					for (int l = 0; l < loops.get(stub1.get(0)).size(); l++) {
						numTwo.add(loops.get(stub1.get(0)).get(l));
					}
					if(areTheyNonTouching(numOne, numTwo)){
						LinkedList<Integer> dump = new LinkedList<Integer>();
						dump.add(stub1.get(0));
						subDelta*=loopGains[stub1.get(0)];
						LinkedList<Integer> stub = new LinkedList<>();
						stub = nonTouching[i-1].get(j);
						for (int l = 0; l < stub.size(); l++) {
							subDelta*=loopGains[stub.get(l)];
							dump.add(stub.get(l));
						}
						nonTouching[i].add(dump);
						if((i%2)!=0)subDelta*=(-1);
						delta+=subDelta;
					}
					
					
				}
			}
		}
		return delta;
	}
	
	public boolean areTheyNonTouching(LinkedList<Integer> ll1 , LinkedList<Integer> ll2){
		HashMap<Integer, Integer> checker = new HashMap<Integer, Integer>();
		for(int i=0;i<ll1.size();i++){
			checker.put(ll1.get(i), ll1.get(i));
		}
		for(int i=0;i<ll2.size();i++){
			if(checker.containsKey(ll2.get(i))) return false;
		}
		return true;
	}
	
	public void printDeltasAndTotalGain(){
		for(int i=0;i<deltas.length;i++){
			System.out.println("Delta "+i+" = "+deltas[i]);
		}
		System.out.println("TotalGain = "+TotalGain);
	}
	
	public void calculateDeltas(){
		deltas = new int[forwardPaths.size()+1];
		for(int i=0;i<(deltas.length-1);i++){
			LinkedList<Integer> dummyo = new LinkedList<>();
			dummyo = forwardPaths.get(i);
			int value = getNonTouchedLoops(dummyo);
			if(value==0)return;
			deltas[i+1] = value;
		}
		LinkedList<Integer> fpp = new LinkedList<>();
		deltas[0] = getNonTouchedLoops(fpp);
	}
	
	public void calculateTotalGain(){
		int totalGain = 0;
		for(int i=0;i<forwardPaths.size();i++){
			totalGain+=(forwardGains[i]*deltas[i+1]);
		}
		totalGain/=deltas[0];
		TotalGain = totalGain;
	}
	
	public void fillSFG(){
		for(int i=0;i<AdjMatrix.length;i++){
			for(int j=0;j<AdjMatrix.length;j++){
				if(AdjMatrix[i][j]!=0){
					this.SFG[i].add(j);
				}
			}
		}
	}
	
	public void Main(){//before calling main you have to make an instance of TheBrain
		
		fillSFG();
		boolean[] visited = new boolean[AdjMatrix.length];
		LinkedList<Integer> pathdump = new LinkedList<Integer>();
		extractForwardPaths(startNode, endNode, startNode, visited, pathdump);
		extractLoops();
		getForwardGains();
		getLoopGains();
		printForwardPaths();
		System.out.println("----------------------------------------------");
		printLoops();
		
		
		calculateDeltas();
		calculateTotalGain();
		printDeltasAndTotalGain();
		
		for (int i = 1; i < nonTouching.length; i++) {
			System.out.println("Level = "+i);
			for (int j = 0; j < nonTouching[i].size(); j++) {
				LinkedList<Integer> dump = new LinkedList<Integer>();
				dump = nonTouching[i].get(j);
				for (int k = 0; k < dump.size(); k++) {
					System.out.print(dump.get(k)+" ");
				}
				System.out.println();
			}
			System.out.println("************************************************");
		}
		
		ProgramWindow.getInstance().showResults(forwardPaths,forwardGains,loops,loopGains,deltas,TotalGain,nonTouching);
	}
	
//	public static void main(String[] args) {
//		int[][] graph = new int[4][4];
//		graph[0][1] = 1;
//		graph[1][2] = 1;
//		graph[2][3] = 1;
//		TheBrain b = new TheBrain(graph, 0, 3);
//		b.Main();
//	}

}
