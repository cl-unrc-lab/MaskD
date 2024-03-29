package model;
import java.util.*;

//Utility class
public class Pair {
	
	private Object fst;
	private Object snd;

	public Pair(Object f, Object s){
	  fst = f;
	  snd = s;
	}

	public Object getFst(){
		return fst;
	}

	public Object getSnd(){
		return snd;
	}

	@Override
	public boolean equals(Object o){
		if (o instanceof Pair){
			Pair p = (Pair)o;
			return p.getFst().equals(fst) && p.getSnd().equals(snd);
		}
		return false;
	}

	public String toString(){
		return "("+fst.toString()+","+snd.toString()+")";
	}


	@Override
	public int hashCode(){
	    return Objects.hash(fst, snd);
	}

}