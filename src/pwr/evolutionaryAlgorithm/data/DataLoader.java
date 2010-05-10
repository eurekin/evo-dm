package pwr.evolutionaryAlgorithm.data;

import pwr.evolutionaryAlgorithm.Configuration;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author pawel
 */
public class DataLoader {

    protected static String trainDataFileName;
    protected static String testDataFileName;
    protected static DataSource trainData;
    protected static DataSource testData;
    static private int dataArguments;
    static protected ArrayList<String> dataClassNames;
    static private float dataArgMin[];
    static private float dataArgMax[];
    static private int crossValidationUpdate = 0;
    static private ArrayList<ArrayList<Record>> CVstructure;

    /*
     * split randomly data into N parts
     * before that all data is in trainData
     */
    public static void doCrossvalidation() {

        //init
        crossValidationUpdate = 0;
        int parts = Configuration.getConfiguration().getCrossvalidationValue();
        CVstructure = new ArrayList<ArrayList<Record>>(parts);
        for (int i = 0; i < parts; i++) {
            CVstructure.add(i, new ArrayList<Record>());
        }

        //split data
        DataSource AllData = new DataSource(trainData);
        AllData.OrganizeData();
        int elements = AllData.size() / parts;
        for (int p = 0; p < parts; p++) {
            for (int e = 0; e < elements; e++) {
                Record Rd = AllData.removeRandomRecord();
                CVstructure.get(p).add(Rd);
            }
        }
        if (AllData.size() > 0) {
            int rest = AllData.size();
            for (int r = 0; r < rest; r++) {
                Record Rd = AllData.removefirstRecord();
                CVstructure.get(r).add(Rd);
            }
        }

        for (int i = 0; i < CVstructure.size(); i++) {
            int s = CVstructure.get(i).size();
            System.out.print(" " + i + "[" + s + "] ");
        }
        /// 0 TEST 1...N TRAIN
        // 0 - fill Test
        // p=1..N-1 - fil train
        initCrossvalidationFold(0);
    }

    public static void doCrossvalidationNext() {
        crossValidationUpdate++;
        initCrossvalidationFold(crossValidationUpdate);
    }

    private static void initCrossvalidationFold(int fold) {
        int parts = Configuration.getConfiguration().getCrossvalidationValue();
        testData.clear();
        trainData.clear();
        for (int part = 0; part < parts; part++) {
            for (Record r : CVstructure.get(part)) {
                if (part == fold) {
                    testData.addRecord(r);
                } else {
                    trainData.addRecord(r);
                }
            }
        }
        testData.OrganizeData();
        trainData.OrganizeData();
    }

    public static int getArgumentsNo() {
        return dataArguments;
    }

    public static int getClassId(String S) {
        return dataClassNames.indexOf(S);
    }

    public static DataSource getTrainData() {
        return trainData;
    }

    public static DataSource getTestData() {
        return testData;
    }

    public static int getClassNumber() {
        return dataClassNames.size();
    }

    public static String getClassName(int i) {
        return dataClassNames.get(i);
    }

    static float getArgMin(int argID) {
        return dataArgMin[argID];
    }

    static float getArgMax(int argID) {
        return dataArgMax[argID];
    }

    public static DataLoader getDataLoader(Configuration config) {
        if (config.isImageDataConfiguration() == true) {
            /**
             * todo: only ECCV_2002 -- insert universal code here
             */
            return new DataLoader(config.getImageWordsFilename(),
                    config.getImageDocWordFilename(),
                    config.getImageBlobCountsFilename(),
                    config.getImageBlobsFilename(),
                    config.getImageTESTDocWordsFilename(),
                    config.getImageTESTBlobCountsFilename(),
                    config.getImageTESTBlobsFilename());
        } else {
            return new DataLoader(
                    config.getTrainFileName(),
                    config.getTestFileName());
        }
    }

    /**
     * main constructor of DataSource class
     * @param TrainDataFileName_ name of file with data for training
     * @param TestDataFileName_ name of file with data for tests
     */
    public DataLoader(String TrainDataFileName_, String TestDataFileName_) {
        testDataFileName = TestDataFileName_;
        trainDataFileName = TrainDataFileName_;

        testData = new DataSource();
        trainData = new DataSource();

        dataClassNames = new ArrayList<String>();

        dataArgMax = null;
        dataArgMin = null;

        trainData = this.LoadCSVFile(trainDataFileName);
        if (testDataFileName.length() != 0) {
            testData = this.LoadCSVFile(testDataFileName);
        }

        Configuration.setClassesNo(getClassNumber());

        //@todo: CROSSVALIDATION on GRAPHical files 
        //else SplitData( Configuration.getConfiguration().getCrossvalidationValue() );
        //trainData.OrganizeData();
        //testData.OrganizeData();
    }

    public DataLoader(String words,
            String document_words,
            String blob_counts,
            String blobs,
            String test_document_words,
            String test_blob_counts,
            String test_blobs) {

        testDataFileName = "";
        trainDataFileName = "";

        testData = new DataSource();
        //trainData = new DataSource();

        dataClassNames = new ArrayList<String>();

        dataArgMax = null;
        dataArgMin = null;

        trainData = LoadImages(words, document_words, blob_counts, blobs, false);

        if (test_document_words.length() != 0) {
            testData = LoadImages(words, test_document_words, test_blob_counts, test_blobs, true);
        }
        //@todo: CROSSVALIDATION on GRAPHical files 
        //else SplitData( Configuration.getConfiguration().getCrossvalidationValue() );
        //trainData.OrganizeData();
        //testData.OrganizeData();

        Configuration.setClassesNo(getClassNumber());
    }

    /// separator <tab> -> each value in ", eg. "1.0" end line -> new record
    private DataSource LoadCSVFile(String FileName) {
        DataSource DS = new DataSource();
        try {
            BufferedReader br = new BufferedReader(new FileReader(FileName));
            String strLine = br.readLine();
            int from, to;
            ///////////////////// number of fields? ///////////////////////
            dataArguments = strLine.split("\t").length - 1;
            //////////////////////////////////////////////////////////////////////////

            boolean MinMaxWasNull = false;
            if (dataArgMax == null) {
                dataArgMax = new float[dataArguments];
                MinMaxWasNull = true;
            }
            if (dataArgMin == null) {
                dataArgMin = new float[dataArguments];
                MinMaxWasNull = true;
            }


            ///// READ FIELDS //////////////////////////////////////////
            int id = 0; // Coevolution
            while (strLine != null && strLine.length() != 0) {
                from = 0;
                to = 0;
                float[] tab = new float[dataArguments];
                for (int i = 0; i < dataArguments; i++) {
                    to = strLine.indexOf("\t", from);
                    tab[i] = Float.valueOf(strLine.substring(from + 1, to - 1)).floatValue();
                    from = to + 1;

                    ///////////////////// MIN & MAX values /////////////////////

                    float tym_value = tab[i];

                    if (i == 0 && MinMaxWasNull == true) {
                        dataArgMax[i] = dataArgMin[i] = tym_value;  //initalisation
                    }
                    if (dataArgMax[i] < tym_value) {
                        dataArgMax[i] = tym_value;
                    }
                    if (dataArgMin[i] > tym_value) {
                        dataArgMin[i] = tym_value;
                    }

                    ////////////////////////////////////////////////////////////
                }
                //// READ class ID //////////////////////////////////////
                String StringClassID = strLine.substring(from + 1, strLine.length() - 1);
                if (!dataClassNames.contains(StringClassID)) {
                    dataClassNames.add(StringClassID);
                }
                int class_id_ = DataLoader.getClassId(StringClassID);

                DS.addRecord(new Record(id++, tab, class_id_));
                strLine = br.readLine();
            }
            //Close the input stream
            br.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return DS;
    }

    /**
     * makes a DataSource from gieven set of 4 files (only for images!)
     * @param words filename
     * @param document_words name of file that connects words and images
     * @param blob_counts name of file that contains number of segments in each image
     * @param blobs name of file that contain segments
     * @return DataSource with all data
     */
    public DataSource LoadImages(String words, String document_words, String blob_counts, String blobs, boolean test) {
        DataSource DS = new DataSource();

        //////////////READING & REGISTERING WORDS.... //////////////////////////
        try {
            dataClassNames.add(null); //there is no 0 class
            FileInputStream fstream = new FileInputStream(words);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine = br.readLine();
            while (strLine != null) {
                if (!dataClassNames.contains(strLine)) {
                    dataClassNames.add(strLine);
                }
                strLine = br.readLine();
            }
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

///////////// READING IMAGES & SEGMENTS ////////////////////////////////////////
        try {
            FileInputStream fstreamDocWords = new FileInputStream(document_words);
            DataInputStream in = new DataInputStream(fstreamDocWords);
            BufferedReader brWords = new BufferedReader(new InputStreamReader(in));
            String strLineWords = brWords.readLine();

            ///////////////////// number of words? ///////////////////////
            int WordsInImage = 0;
            int from = 0, to = 0;
            do {
                to = strLineWords.indexOf(" ", from);
                WordsInImage++;
                from = to + 1;
            } while (to < strLineWords.lastIndexOf(" "));
            ////////////////////////////////////////////////////////////////////
            if (test == false) {
                dataArguments = Configuration.getConfiguration().getNumberOfAttributes();
                if (dataArgMax == null) {
                    dataArgMax = new float[dataArguments];
                }
                if (dataArgMin == null) {
                    dataArgMin = new float[dataArguments];
                }
            }

            /**
             * todo:     data
             *
             * boolean MinMaxWasNull = false;
            if (Data_arg_max == null)  {Data_arg_max = new float[Data_arguments];MinMaxWasNull=true;}
            if (Data_arg_min == null)  {Data_arg_min = new float[Data_arguments];MinMaxWasNull=true;}

             *
             */
            /////////////// Open file with number of semegents
            FileInputStream fstreamDocSementsInfo = new FileInputStream(blob_counts);
            DataInputStream in2 = new DataInputStream(fstreamDocSementsInfo);
            BufferedReader brSegementsInfo = new BufferedReader(new InputStreamReader(in2));

            ///////////// open file with segments.............
            FileInputStream fstreamDocSements = new FileInputStream(blobs);
            DataInputStream in3 = new DataInputStream(fstreamDocSements);
            BufferedReader brSegements = new BufferedReader(new InputStreamReader(in3));

            while (strLineWords != null) {

                /////////// number of segments?
                String strLineSegmentsInfo = brSegementsInfo.readLine();
                int segments = Integer.parseInt(strLineSegmentsInfo);
                String[] segmentsTab = new String[segments];
                for (int i = 0; i < segments; i++) {
                    String strLineSegments = brSegements.readLine();
                    segmentsTab[i] = strLineSegments;
                }
                RecordImage R = new RecordImage(strLineWords, segmentsTab);
                DS.addRecord(R);
                ///////////////////////// for each argument.....
                for (int a = 0; a < dataArguments; a++) {
                    if (DS.size() == 1) { ///first record
                        dataArgMax[a] = R.getMaxAttribValue(a);
                        dataArgMin[a] = R.getMinAttribValue(a);
                    }
                    if (R.getMaxAttribValue(a) > dataArgMax[a]) {
                        dataArgMax[a] = R.getMaxAttribValue(a);
                    }
                    if (R.getMinAttribValue(a) < dataArgMin[a]) {
                        dataArgMin[a] = R.getMinAttribValue(a);
                    }
                }
                ///////////////////////////////////////////////////////////////
                strLineWords = brWords.readLine();
            }
            //Close the input streams
            in.close();
            in2.close();
            in3.close();

            dataArguments = Configuration.getConfiguration().getNumberOfAttributes();

        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        ///////////////////////////////////////

        return DS;
    }

    public static String FileSummary() {
        StringBuilder s = new StringBuilder("");
        s.append(" dataFile:");
        s.append(trainDataFileName);
        s.append(" inst.:");
        s.append(trainData.size());
        s.append(" atrib:");
        s.append(dataArguments);
        s.append(" classes:");
        s.append(dataClassNames.size());
        s.append(" [");
        for (int i = 0; i < dataClassNames.size(); i++) {
            s.append(" " + i + "'" + dataClassNames.get(i) + "'");
        }
        s.append(" ]");
        s.append(" Data(train=");
        s.append(trainData.size());
        s.append(", test=");
        s.append(testData.size());
        s.append(")");
        return s.toString();
    }
}
