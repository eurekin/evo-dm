package pwr.evolutionaryAlgorithm.data;



import java.util.ArrayList;

/**
 *
 * @author pawelm
 */
public class DataSet {

    private float Precision = 0;
    private float Recall = 0;
    private float Accuracy = 0;
    private float Fsc = 0;
    
    
    public void setEvaluation(float prec, float rec, float acc, float fsc){
        this.Precision = prec;
        this.Recall = rec;
        this.Accuracy = acc;
        this.Fsc = fsc;
    }
    
      
    public float getFsc(){
    	return this.Fsc;
    }

    
    public float getPrecision(){
        return Precision;
    }
    
      

    public float getRecall(){
        return this.Recall;
    }
    
      

    public float getAccuracy(){
        return this.Accuracy;
    }
    
      

    /**
     * @todo LinkedHashSet na iteratorach!?
     */
    private ArrayList<Record> records;
    
      

    public Record getRecord(int i){
        return this.records.get(i);
    }
    
      

    public DataSet(  ){
    this.records = new ArrayList<Record>();
    }
    
      

    public DataSet( DataSet D ){
    this.records = new ArrayList<Record>( D.records );
    }
    
      

    public void removeRecord(final Record R){
        this.records.remove(R);
    }
    
      

    public void addRecord(final Record R){
        this.records.add(R);
    }
    
      

   public long elements(){
      return this.records.size();
    }
   
     

    public boolean empty(){
      if (this.records.size()==0) return true;
      else return false;
    }
    
      

    public void clear(){
        this.records.clear();
    }
    
      

    public boolean contains(final Record R){
        return this.records.contains(R);
    }
    
      
    /**
     * @todo prosty kod! poprawic na linkedshaset (?)
     * @param DS1
     * @param DS2
     * @return
     */
    static public DataSet OperatorPlus(final DataSet DS1, final DataSet DS2){

        DataSet Result = new DataSet( DS1 );

        //Result.records.addAll( DS1.records);

        //adding form DS2 if not already in DS1
        for (int e=0;e<DS2.elements();e++){
            Record R = DS2.records.get(e);
            if (Result.records.contains(R)==false) Result.records.add(R);
        }
        return Result;
    }
    
      

    /**
     * returns DS1 - DS2
     * @param DS1 DataSet1
     * @param DS2 DataSet2
     * @return DS1 - DS2
     */
   static public DataSet OperatorMinus(final DataSet DS1, final DataSet DS2){
       DataSet Result = new DataSet( DS1 );
        
       for (int e=0;e<DS1.elements();e++){
             Record R = DS1.records.get(e);
             if (DS2.records.contains(R)==true) Result.records.remove(R);
        }
        return Result;
   }
   
     
    @Override
   public String toString(){
       StringBuilder SB = new StringBuilder();
       for (int r=0;r<records.size();r++) SB.append( records.get(r).toString()+";;;\n");
       return SB.toString();
   }
  
    
      
    
}

