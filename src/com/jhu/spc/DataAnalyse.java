package com.jhu.spc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;

public class DataAnalyse {
	
	Map<Integer,double[]> index2vec=new HashMap<Integer,double[]>();
	Map<Integer,String> index2attribute=new HashMap<Integer,String>(); //match?
    public static double C = 10;
    double eps = 0.1;
	int dimension;
	
	public DataAnalyse(int dimension){
		this.dimension=dimension;
	}
	
	public void Train(File data, File saveModel) throws Exception{//data file; model saving location
		FileReader fr=new FileReader(data);
		BufferedReader bf=new BufferedReader(fr);
		String readFile;
		while((readFile=bf.readLine())!=null){
			String[] s=readFile.split(" ");
			int index=Integer.valueOf(s[0]);
			double[] vec=new double[dimension];
			for(int i=0;i<vec.length;i++){
				vec[i]=Double.parseDouble(s[i+1]);
			}
			index2vec.put(index, vec);
			index2attribute.put(index, s[s.length-1]);
		}
		bf.close();
		TrainModel(saveModel);
	}
	
	public void TrainModel(File saveModel){
		int trainNum=index2attribute.keySet().size();
		double trainAttr[]=makeTrainAttr(trainNum);
		Feature vectrain[][]=makeFeature(trainNum);
		Problem problem=new Problem();
		problem.l=trainNum;
		problem.n=dimension;
		problem.x=vectrain;
		problem.y=trainAttr;
		SolverType s=SolverType.MCSVM_CS;  
        Parameter parameter = new Parameter(s, C, eps);
        Model modelg = Linear.train(problem, parameter);
        try {
			modelg.save(saveModel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public double Evaluate(double[] vecNew, File saveModel) throws Exception{
		double resultAttr=0;
		Feature[] vectest=new Feature[dimension];
		for(int j=0;j<dimension;j++){
			vectest[j]=new FeatureNode(j+1,vecNew[j]);
		}
		Model model=Model.load(saveModel);
		resultAttr=Linear.predict(model,vectest);
		return resultAttr;
	}
	
	public Feature[][] makeFeature(int trainNum){
		Feature vectrain[][]=new Feature[trainNum][dimension];
		Set<Integer> ks=index2attribute.keySet();
		int count=0;
		for(int i:ks){
			double[] itemVec=index2vec.get(i);
			for(int j=0;j<dimension;j++)
				vectrain[count][j]=new FeatureNode(j+1, itemVec[j]);
			count++;
		}
		return vectrain;
	}
	
	public double[] makeTrainAttr(int trainNum){
		double[] trainAttr=new double[trainNum];
		Set<Integer> ks=index2attribute.keySet();
		int count=0;
		for(int i:ks)
			trainAttr[count++]=Double.parseDouble(index2attribute.get(i));
		return trainAttr;
	}

}
