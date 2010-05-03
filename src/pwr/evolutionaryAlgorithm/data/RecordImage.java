package pwr.evolutionaryAlgorithm.data;

import java.util.ArrayList;

/**
 *
 * @author pawelm
 */
class RecordImage extends Record {

    static public float END_VALUE = -99f;
    static public String TAB = "\t";
    static public String SPACE = " ";

    private float [][] values;
    private int Segments;
    static private int WordsInImage;
    static private int VectorSize;
    protected ArrayList<Integer> ClassesNames;

    private int currentClass = 0;
    private int currentSegment = 0;

//------------------------------------------------------------------------------
public RecordImage(String words, String [] segments) {
       ClassesNames = new ArrayList<Integer>();    
          ////// number o words?
           WordsInImage = 0;
            int from=0, to =0;
            do{
                to = words.indexOf(SPACE, from);
                int i = Integer.parseInt(words.substring(from, to));
                if (i!=END_VALUE){
                    ClassesNames.add(i);
                    WordsInImage++;
                    }
                from = to+1;
            }while (to<words.lastIndexOf(SPACE));
        ////////////////////////////////////////
          ////// size of segment?
           VectorSize = 0;
            from=0; to =0;
            do{
                to = segments[0].indexOf(TAB, from);
                VectorSize++;
                from = to+1;
            }while (to<segments[0].lastIndexOf(TAB));
            VectorSize++;
  ///////////////////////////////////////          
        Segments = segments.length;
        this.values = new float[Segments][VectorSize];
        for (int s=0;s<Segments;s++){
            from=0; to =0;
            for (int v=0;v<VectorSize;v++){
                to = segments[s].indexOf(TAB, from);
                if (to==-1)
                    to=segments[s].length();
                float f = Float.parseFloat(segments[s].substring(from, to));
                this.values[s][v] = f;
                from = to+1;
            }
        }
    }

//------------------------------------------------------------------------------

 @Override
public float getArgumentValue(int argId){
   this.currentSegment = 0;
   return values[currentSegment][argId];
}

//------------------------------------------------------------------------------

 public float getArgumentValueNext(int argId){
   float result = END_VALUE;
   if (currentSegment>=Segments-1)  result=END_VALUE;
   else {
       currentSegment++;
       result = this.values[this.currentSegment][argId];
   }
   return result;
 }

 //------------------------------------------------------------------------------

 @Override 
 public boolean hasClass(int class_id){
	 for (int c=0;c<ClassesNames.size();c++){
		 if (class_id==ClassesNames.get(c)) return true; 
	 }
	return false;
}
 
//------------------------------------------------------------------------------
 
@Override
public int getClassName(){
    currentClass = 0;
    return ClassesNames.get(currentClass);
}

//------------------------------------------------------------------------------

 @Override
public int getClassNameNext(){
    if (currentClass>=ClassesNames.size()-1) return -1;
    currentClass++;
    return ClassesNames.get(currentClass);
}

//------------------------------------------------------------------------------


public void DoScaleAtribute(int attribID, float max){
  for (int s=0;s<Segments;s++){
      this.values[s][attribID] = this.values[s][attribID]/max;
  }  
}


//------------------------------------------------------------------------------

@Override
public boolean isSatisfy(Condition C){

    /**
     * TODO x,y,z in the same segment!
     */
    
    for (int s=0;s<Segments;s++){
            switch (C.getRelation()){
                    case IN :    if ( this.values[s][ C.getAttrib() ] >= C.getValue1() && this.values[s][ C.getAttrib() ] <= C.getValue2()) return true;
                                      // else return false;
                    case NOT_IN :  if ( this.values[s][ C.getAttrib() ] < C.getValue1() || this.values[s][ C.getAttrib() ] > C.getValue2()) return true;
                                           //else return false;
                    }
    }
    return false;
}

//------------------------------------------------------------------------------

    @Override
public float getMaxAttribValue(int attrib){
    float max =  this.values[0][attrib];
    for (int s=0;s<Segments;s++){
        if (this.values[s][attrib]>max) max=this.values[s][attrib];
    }
    return max;
}

//------------------------------------------------------------------------------

    @Override
public float getMinAttribValue(int attrib){
    float min =  this.values[0][attrib];
    for (int s=0;s<Segments;s++){
        if (this.values[s][attrib]<min) min=this.values[s][attrib];
    }
    return min;
}

 //------------------------------------------------------------------------------

}
