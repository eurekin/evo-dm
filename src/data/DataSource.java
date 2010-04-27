package data;

import java.util.*;
import utils.Rand;



// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.6DE6F2C3-16BB-9A99-5AB5-0F165CB246CC]
// </editor-fold> 
 
public class DataSource {

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.329BBDCE-15F9-7EFE-C03A-3AFB158429E2]
    // </editor-fold> 
    private ArrayList<Record> Data;
  
    ///////////////////////////////////////////////////////////////////////////
    private long Data_Expected_by_Class[];

    /////////////////////////////////////////////////////
    
     public class Linker{
           public Linker(float v_, Record R_) {
               v = v_;
               R=R_;
           }

           public int compareTo( float key) {
                if (this.v > key) return 1;
                else if (this.v < key) return -1;
            return 0;
            }

            public float v;
           public Record R;
        }

     class comparator implements Comparator<Linker>{
        public int compare(Linker o1, Linker o2) {
                if (o1.v > o2.v) return 1;
                else if (o1.v < o2.v) return -1;
            return 0;
            }
       
        }

//------------------------------------------------------------------------------

     /**
      * BinarySearch with range modyfications
      * @param attrib number of attribute (0..N)
      * @param key value to search
      * @param left if there is a few record with the same value we return the left (first) or right (last) one
      * @return position in table where key should be placed
      */
      private int binarySearch( int attrib, float key, boolean left)
    {
        int low = 0;
        int high = INDEX.get(attrib).size()-1;
        int mid = 0;

        if (key<INDEX.get(attrib).get(0).v) return 0;
        if (key>INDEX.get(attrib).get(high).v) return high;

        while( low <= high )
        {
            mid = ( low + high ) / 2;

            Linker L = INDEX.get(attrib).get(mid);
            if( L.compareTo( key ) < 0 )
                low = mid + 1;
            else if( L.compareTo( key ) > 0 )
                high = mid - 1;
           else break;
          //   return mid;
        }
        //if (mid!=0 || mid!=INDEX.get(attrib).size()-1) return mid;
        //else return -1;     // NOT_FOUND = -1

        if (mid>1 && INDEX.get(attrib).get(mid).v==key)
        {
         if (mid>0 && left==true){
          while (true){
              if (mid>0)
                {
                if (INDEX.get(attrib).get(mid).v==INDEX.get(attrib).get(mid-1).v) mid--;
                else return mid;
                }
              else return mid;
           }
         }

        if (left==false && mid<INDEX.get(attrib).size()-1 && mid>1){
          while (true){
              if (mid<INDEX.get(attrib).size()-1)
                  {
                  if (INDEX.get(attrib).get(mid).v==INDEX.get(attrib).get(mid+1).v) mid++;
                  else return mid;
                  }
              else return mid;
                }
            }
        }

        return mid;
    }

     ////////////////////////////////////////////////////

    private ArrayList<ArrayList<Linker>> INDEX;
   
    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.1977EB43-1547-B187-F401-01B904A9A91D]
    // </editor-fold>
    /**
     * main constructor of DataSource class
     * @param TrainDataFileName name of file with data for training
     * @param TestDataFileName name of file with data for tests
     */
    public DataSource () {
        Data = new ArrayList<Record>();
    }

//  ------------------------------------------------------------------------------
    
    public DataSource (DataSource DS){
    	if (DS.Data!=null) Data = ( DS.Data );
    	else Data = new ArrayList<Record>();
    	
    	if (DS.INDEX!=null) INDEX = new ArrayList<ArrayList<Linker>> (DS.INDEX);
    	else INDEX = null;
    	
    	if (DS.Data_Expected_by_Class!=null){
    		int classes =  DataLoader.getClassNumber();
    		Data_Expected_by_Class = new long[classes];
    			for (int i=0;i<classes;i++){
    				Data_Expected_by_Class[i] = DS.Data_Expected_by_Class[i];	
    				}
    	}
    	else Data_Expected_by_Class = null;
    }
    
//------------------------------------------------------------------------------

    public boolean addRecord(Record R){
        if (Data.add(R)) return true;
        else return false;
    }
    
//  ------------------------------------------------------------------------------    

    public Record removefirstRecord(){
        if (Data.size()<1) return null;
        else{
        	Record Rd = Data.remove(0);
        	return Rd;
        }
    }
    
//------------------------------------------------------------------------------
    
    public Record removeRandomRecord(){
    	int r = Rand.GetRandomInt( Data.size());
    	Record R = Data.remove(r);
    	return R;
    }
    
//------------------------------------------------------------------------------
  
    public void clear(){
    	if (Data!=null) Data.clear();
    	Data_Expected_by_Class = null;	
    	if (INDEX!=null) INDEX.clear();
    }
    
//  ------------------------------------------------------------------------------
    
    public int size(){
        return Data.size();
    }

//------------------------------------------------------------------------------

    public Record get(int i){
        return Data.get(i);
    }

//------------------------------------------------------------------------------

    /**
    * method of data organization into collections
    */
    public void OrganizeData(){

        //////////////////////////////////////////////////////////////////////////////////////////
      //Expected - for each class
       Data_Expected_by_Class = new long[ DataLoader.getClassNumber() ];
       for (int i=0;i<DataLoader.getClassNumber();i++)
            Data_Expected_by_Class[i]=0;

       int class_id = 0;
       int class_name = -1;
       for (int r=0;r<Data.size();r++) { //for each record

           if (Data.get(r) instanceof RecordImage){
               class_name =  Data.get(r).getClassName();
               do{
                   class_id =class_name;
                   Data_Expected_by_Class[ class_id ] ++;
                   class_name =  ((RecordImage)Data.get(r)).getClassNameNext();
               }while(class_name!=-1);
           }
           else {
               //class_name =  Data.get(r).getClassName();
               class_id = Data.get(r).getClassName();
               Data_Expected_by_Class[ class_id ] ++;
               }
       }
       //////////////////////////////////////////////////////////////////////////////////////////
       ///SCALING NEEDED?
     /* for (int i=0;i<DataLoader.getArgumentsNo();i++) { //for each attribute
         float max = Data.get(0).getArgumentValue(i);

         for (int r=0;r<Data.size();r++){ //add each record
               float tmpValue;
                if (  Data.get(r) instanceof RecordImage)
                 { //add each segment
                   tmpValue = Data.get(r).getArgumentValue(i);
                   do{
                        tmpValue= ((RecordImage)(Data.get(r))).getArgumentValueNext(i);
                        if (tmpValue>max) max=tmpValue;
                   }while(tmpValue != RecordImage.END_VALUE);
                }
                else {
                   tmpValue = Data.get(r).getArgumentValue(i);
                    if (tmpValue>max) max=tmpValue;
               }
           }
         //scaling needed if max>1.0f and RecordImage
         if (max>1.0f && Data.get(0) instanceof RecordImage){
              for (int r=0;r<Data.size();r++) {
                   ((RecordImage)(Data.get(r))).DoScaleAtribute(i, max);
              }

         }
         ////////////////////
      }*/

       //////////////////////////////////////////////////////////////////////////////////////////

       INDEX = new ArrayList<ArrayList<Linker>>(DataLoader.getArgumentsNo());
       for (int i=0;i<DataLoader.getArgumentsNo();i++) { //for each indekser...

           INDEX.add( i, new ArrayList<Linker>() ); //create it

           for (int r=0;r<Data.size();r++){ //add each record
               
               if (Data.get(r) instanceof Record) INDEX.get(i).add(r, new Linker( Data.get(r).getArgumentValue(i) , Data.get(r))  );
               else { //add each segment
                   float value = Data.get(r).getArgumentValue(i);
                   do{
                        INDEX.get(i).add(r, new Linker( value , Data.get(r))  );
                        value = ((RecordImage)(Data.get(r))).getArgumentValueNext(i);
                   }while(value!=-1);
               }
           }
           ///sorting
           Collections.sort(INDEX.get(i), new comparator());
       }

    }

//------------------------------------------------------------------------------
    /**
    *
    * @param c condition
    * @return returns elements from datasource that condition is succeed
    */
   public DataSet getDataSet( Condition c ) {//throws Exception{

       //if (c==null) throw new Exception("Condition is a null!");

       DataSet DSet = new DataSet();

      int point1 = 0;
      int point2 = 0;

        point1 = binarySearch(c.getAttrib(), c.getValue1(), true);
        point2 = binarySearch(c.getAttrib(), c.getValue2(), false);

       if (point2==-1) point2=Data.size()-1;

       if (c.getRelation()==Condition.RelationType.IN){


               if (INDEX.get(c.getAttrib()).get(point1).v >= c.getValue1() && INDEX.get(c.getAttrib()).get(point1).v <= c.getValue2())  DSet.addRecord( INDEX.get(c.getAttrib()).get(point1).R);

               if (point1<point2){
                   for (int r=point1+1;r<point2;r++){
                         DSet.addRecord( INDEX.get(c.getAttrib()).get(r).R);
                   }
               }

             if (INDEX.get(c.getAttrib()).get(point2).v >= c.getValue1() && INDEX.get(c.getAttrib()).get(point2).v <= c.getValue2() && point1!=point2)  DSet.addRecord( INDEX.get(c.getAttrib()).get(point2).R);
          }
       else { //not_in
             //point1 = binarySearch(c.getAttrib(), c.getValue1(), true); //the point where record should be
             //point2 = binarySearch(c.getAttrib(), c.getValue2(), false);

              //////////

               if (point1>0)   {
                   for (int r=0;r<point1;r++){
                   if (INDEX.get(c.getAttrib()).get(r).v < c.getValue1()) DSet.addRecord( INDEX.get(c.getAttrib()).get(r).R);
                   }
               }

               if (INDEX.get(c.getAttrib()).get(point1).v < c.getValue1())  DSet.addRecord( INDEX.get(c.getAttrib()).get(point1).R);

               if (point2+1<Data.size()){
                   for (int r=point2+1;r<Data.size();r++){
                    if (INDEX.get(c.getAttrib()).get(r).v > c.getValue1() && INDEX.get(c.getAttrib()).get(r).v > c.getValue2()) DSet.addRecord( INDEX.get(c.getAttrib()).get(r).R);
                   }
               }
               if (INDEX.get(c.getAttrib()).get(point2).v > c.getValue2())  DSet.addRecord( INDEX.get(c.getAttrib()).get(point2).R);
      }
   return DSet;
   }

   //------------------------------------------------------------------------------

   public DataSet getDataSet( DataSet s, Condition c ){
       DataSet DSet = new DataSet();
        for (int d=0;d<s.elements();d++){
           if (s.getRecord(d).isSatisfy(c)==true) DSet.addRecord(  s.getRecord(d) );
       }
       return DSet;
    }

   //------------------------------------------------------------------------------

    public long getExpected(int classID){
     return Data_Expected_by_Class[classID];
    }

    //------------------------------------------------------------------------------

    /**
     * Returns set of records classified correctly in s dataset
     * @param s set of dataset
     * @param class_id class of classification
     * @return set of records
     */
    public DataSet getCorrect(DataSet s, int class_id){
       DataSet CorrectDS = new DataSet();
       for (int i=0;i<s.elements();i++){
           int RecordClass = s.getRecord(i).getClassName();
           do{
               if (class_id == RecordClass) {
                   CorrectDS.addRecord(s.getRecord(i));
               }
               RecordClass = s.getRecord(i).getClassNameNext();
           }while(RecordClass!=-1);
       }
        return CorrectDS;
    }


//-----------------------------------------------------------------------------

    /*
     * returns report
     * RECORDS_TOTAL
     * RECORDS_IN_CLASS
     */
    @Override
    public String toString(){
 	   StringBuilder SB = new StringBuilder();
 	   
 	   SB.append("\n RECORDS="+this.Data.size()+"  Records_in_class " );
 	   
 		int classes =  DataLoader.getClassNumber();
			for (int i=0;i<classes;i++){
				SB.append(" c"+i+" ["+Data_Expected_by_Class[i]+"] ");	
			}
 	   return SB.toString();
    }
   
    
    
}

