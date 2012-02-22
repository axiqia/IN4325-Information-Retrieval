package nl.tudelft.in4325.a1.normalization;

import java.io.File;

import nl.tudelft.in4325.a1.Constants;
import nl.tudelft.in4325.a1.utils.XmlInputFormat;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Hadoop job to normalise Wikipedia XML corpus
 */
public class NormalizationDriver {

	/**
	 * Type of normalization. Simple just works with spaces tokenization, advanced uses more complex algorithms.
	 */
	private enum NormalizationType{
		SIMPLE(Constants.Jobs.SIMPLE_NORMALIZATION.toString(), SimpleNormalizationMapper.class), 
		ADVANCED(Constants.Jobs.ADVANCED_NORMALIZATION.toString(), AdvancedNormalizationMapper.class);
		
		NormalizationType(String name, Class<? extends Mapper<Object, Text, Text, IntWritable>> mapper){
			this.name = name;
			this.mapper = mapper;
		}
		
		private String name;
		private Class<? extends Mapper<Object, Text, Text, IntWritable>> mapper;
		
		public String getName(){
			return name;
		}
		
		public Class<? extends Mapper<Object, Text, Text, IntWritable>> getMapper(){
			return mapper;
		}
	}
	
	//This variable is responsible for normalization to be applied
	private static final NormalizationType normalizationType = NormalizationType.ADVANCED;
	
	/**
	 * Configures and runs the job with Hadoop
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{

		//reading input and output paths from configuration file
		String propertiesPath = Constants.DEFAULT_PROPERTIES_FILE;
		
		if (new File(Constants.PROPERTIES_FILE).exists()){
			propertiesPath = Constants.PROPERTIES_FILE;
		}
		
		Configuration propertiesConfig = null;
		propertiesConfig = new PropertiesConfiguration(propertiesPath);
		String input = propertiesConfig.getString("source-input");
		String output;
		
		output = propertiesConfig.getString("normalization-output");
	
		//configuring Hadoop and running the job
		org.apache.hadoop.conf.Configuration hadoopConfig = new org.apache.hadoop.conf.Configuration();
		hadoopConfig.set("xmlinput.start","<page>") ;
		hadoopConfig.set("xmlinput.end","</page>") ;
		
        Job job = new Job(hadoopConfig, normalizationType.getName());
        job.setJarByClass(NormalizationDriver.class);
        job.setMapperClass(normalizationType.getMapper());
        job.setReducerClass(NormalizationReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setInputFormatClass(XmlInputFormat.class);
        
        FileInputFormat.addInputPath(job, new Path(input));
        Path outputPath = new Path(output);
        FileOutputFormat.setOutputPath(job, outputPath);
        
        FileSystem dfs = FileSystem.get(outputPath.toUri(), hadoopConfig);
        if (dfs.exists(outputPath)) {
        	dfs.delete(outputPath, true);
        }

        System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}