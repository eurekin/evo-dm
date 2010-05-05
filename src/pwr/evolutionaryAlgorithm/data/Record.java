package pwr.evolutionaryAlgorithm.data;



/**
 *
 * @author pawelm
 */
public class Record {

private int size;
private float [] values;
private int className;

public Record() {
    }

Record( int size, float [] values, int className ){
    this.values = values;
    this.size = size;
    this.className = className;
}

/**
 * returns value of given arguemnt ID
 * @param argId a given ID
 * @return value of selected argument
 */
public float getArgumentValue(int argId){
    return this.values[argId];
}

public int getClassName(){
    return this.className;
}

public int getClassNameNext(){
    return -1;
}


public boolean hasClass(int class_id){
	  if (class_id==this.className) return true;
	  else return false;
}

/*public boolean isSatisfy(Rule R){
    return isSatisfy(R, 0);
}*/
/*
public boolean hasTheSameClass(Rule R){
  if (R.getClassID()==this.className) return true;
  else return false;
}
*/
/*
public boolean isSatisfy(Rule R, int i){

    int ATTR = Configuration.getNumberOfAttributes();
    
    if (i==0) i=-1;
    if (i==ATTR) return false;

    for (int ci = i+1;ci< ATTR; ci++){
        RuleGene G = R.getGene(ci);
        if ( !G.isOff() ){
            if ( this.isSatisfy( G.getCondition(ci) )==false ) 
                return false;
        }
    }

    return true;
}
*/
/*
public boolean isSatisfy(Rule r){
    switch (r.getRelation()){
        case IN :                if ( this.values[ r.getAttrib() ] >= r.getValue1() && this.values[ r.getAttrib() ] <= r.getValue2()) return true;
                                       else return false;
        case NOT_IN :  if ( this.values[ r.getAttrib() ] < r.getValue1() || this.values[ r.getAttrib() ] > r.getValue2()) return true;
                                       else return false;
    }
    return false;
}
*/


public boolean isSatisfy(Condition C){
    switch (C.getRelation()){
        case IN :                if ( this.values[ C.getAttrib() ] >= C.getValue1() && this.values[ C.getAttrib() ] <= C.getValue2()) return true;
                                       else return false;
        case NOT_IN :  if ( this.values[ C.getAttrib() ] < C.getValue1() || this.values[ C.getAttrib() ] > C.getValue2()) return true;
                                       else return false;
    }
    return false;
}


public float getMaxAttribValue(int attrib){
   return  this.values[attrib];
}


public float getMinAttribValue(int attrib){
    return  this.values[attrib];
}

    @Override
public String toString(){
    StringBuilder SB = new StringBuilder();

    for (int i=0;i<size;i++){
        SB.append(values[i]+";");
    }
    return SB.toString().replace(".", ",");
}

}
