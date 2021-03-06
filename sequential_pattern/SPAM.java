import java.io.* ;
import java.util.* ;
import java.lang.Math ;
import jdsl.core.algo.traversals.*;
import jdsl.core.api.*;
import jdsl.core.ref.*;

public class SPAM{
	private Tree tree = new NodeTree();

	private int powk ;
	private int cluster ;
	private int maxTransSize ;
	private int T ;
	private Hashtable hash = new Hashtable() ;
	private Hashtable hash_value = new Hashtable() ;
	private Hashtable hash_pos = new Hashtable() ;
	List<String> seed = new ArrayList<String>();
	List<String> cur_node = new ArrayList<String>();
	List<String> tree_order_list = new ArrayList<String>();

	public List<String> createLevel(List<String> cur_node , List<String> seed , int level){
		// s_step level
		List<String> new_node = new ArrayList<String>() ;
		for(String node : cur_node) {		
			for(String sd : seed){
				// count s_step
				int[] result = s_step(getHash(node) , getHash(sd)) ;
				String newnode = node+"->"+sd ;
				// put hash	
				addHash( newnode , result) ;
				addValue( newnode , sumArray(result) ) ;
				addOrderList(newnode , result , level) ;
				// put into display tree
				insertDispTree(node , newnode) ;

				// add to new_node , check if node size too big
				String[] leaf = (newnode).split("->") ;
				// set condition
				if(leaf.length < maxTransSize && sumArray(result) >= T )
					new_node.add(newnode) ;
				
				// count i_step , find some number
				String[] i_string = node.split("->") ;
				String i_string_part = i_string[i_string.length-1] ;
				String[] i_element = i_string_part.split(":") ;
				String i_string_ele = i_element[i_element.length-1] ;
				if(i_string_part.indexOf(sd) == -1 && seed.indexOf(i_string_ele) < seed.indexOf(sd) ){
					result = i_step( getHash(node) , getHash(sd)) ;
					newnode = node+":"+sd ;
					// put hash
					addHash(newnode,result) ;
					addValue( newnode , sumArray(result) ) ;
					addOrderList(newnode , result , level) ;
					// put into display tree
					insertDispTree(node , newnode) ;
					// set condition
					if( sumArray(result) >= T) 
						new_node.add(newnode) ;
				}
			}
		}
		return new_node ;
	}

	public void insertDispTree(String par , String s){
		Position p = null ;
		// retri from pos hash
		if(par.equals("root")){
			p = tree.root() ;
		}else{
			p = getPos(par) ;
		}

		Position newp = tree.insertLastChild(p , s+" "+Integer.toString((getValue(s)).intValue())) ;

		// pos hash
		addPos(s,newp) ;
	}

	public void buildTree() {
		List<String> cur_node = new ArrayList<String>() ;
		for(String s : seed){
			cur_node.add(s) ;
			addValue( s , sumArray(getHash(s)) ) ;
			// tree initial
			insertDispTree("root" , s) ;
		} 

		// create level 
		int level = 0 ;
		while( !cur_node.isEmpty() ){
			cur_node = createLevel(cur_node , seed , level) ;
			level++ ;	
		}
	}
	public void setT(int t){
		T = t ;
	}

	public void setPowk(int k){
		powk = k ;
	}
	public void setMaxTransSize(int c){
		maxTransSize = c;
	}
	
	public void setCluster(int c){
		cluster = c ;
	}

	public void addOrderList(String key , int[] value , int level){
		String tap = "" ;
		for(int i=0 ; i<level ; i++)
			tap+="\t" ;
		
		tree_order_list.add(tap + key+"\t"+Arrays.toString(value)+" "+sumArray(value)) ;
	}

	public void addHash(String key){
		int[] array = new int[powk*cluster] ;
		Arrays.fill(array, 0) ;
		hash.put(key , array) ;
	}
	public void addHash(String key, int[] array){
		hash.put(key , array) ;
	}
	
	public void addPos(String key, Position pos){
		hash_pos.put(key,pos) ;
	}
	public void addValue(String key, int i){
		hash_value.put(key , new Integer(i)) ;
	}

	public Integer getValue(String key){
		return (Integer) hash_value.get(key) ;
	}
	public Position getPos(String key){
		return (Position) hash_pos.get(key) ;
	}
	public int[] getHash(String key){
		   return (int[]) hash.get(key) ;
	}

	public void printDispTree(){
		System.out.println(tree.toString()) ;
	}

	public void printHash(){
		Set s ;
		s = hash.keySet() ;

		Iterator<String> it = s.iterator();
 
		while ( it.hasNext() == true )
		{
		   String key = it.next();
		   System.out.print( key+"\t" );

		   int[] t ;
		   t =(int[]) hash.get(key) ;

		   for(int i=0 ; i<t.length ; i++){
			if(i%powk == 0){
				System.out.print("#") ;
			}

			System.out.format("%2d " , t[i]) ;
			//System.out.print( t[i]+" " ) ;
		   }
		   System.out.print("# Sum: ") ;
		   System.out.print( sumArray(t) ) ;
		   System.out.print("\n") ;
		}

	}
	public void printOrderList(){
		for(String s : tree_order_list ){
			System.out.println(s) ;
		}
	}	

	public void printArray(int[] array){

		for(int i=0 ; i<array.length ; i++){
			System.out.print(array[i]+" ") ;
		}
		System.out.print("\n") ;
	}

	public boolean hasKeyHash(String s){
		return hash.containsKey(s) ;
	}

	public int[] s_step_process(int[] data){
		int[] answer = new int[powk*cluster] ;

		for(int base=0 ; base<powk*cluster ; base+=powk) {
			boolean flag=false;
			for(int offset=0 ; offset<powk ; offset++){
				int index = base+offset ;
				if(flag){
					answer[index] = 1 ;	
				}else{ //flag==0
					if(data[index] == 1){
						flag=true ;
					}
					answer[index] = 0 ;
				} 
			}	
		}
		return answer ;
	}

	public int[] s_step(int[] from , int[] to){
		int[] answer = new int[powk*cluster] ;

		// s_process
		answer = s_step_process(from) ;

		// and array
		for(int i=0 ; i<answer.length ; i++){
			if( answer[i]==1 && to[i]==1 ){
				answer[i]=1 ;
			}else{
				answer[i]=0 ;
			}
		}

		return answer ;
	} 
	public int[] i_step(int[] from , int[] to){
		int[] answer = new int[powk*cluster] ;
		// and array
		for(int i=0 ; i<from.length ; i++){
			if( from[i]==1 && to[i]==1 ){
				answer[i]=1 ;
			}else{
				answer[i]=0 ;
			}
		}
		return answer ;
	} 

	public int sumArray(int[] array){
		int sum=0 ;
		for(int i=0 ; i<array.length ; i++)
			sum+=array[i] ;

		return sum ;
	}

	public void setParameter(String input) {
	try{
		BufferedReader in = new BufferedReader(new FileReader(input));
		String s = null ;
		int max_cluster_size=0 ;
		int total_cluster=0 ;
		
		int pre_id=-1 ;
		int cluster_size=0 ;
		while( (s=in.readLine())!= null ){
			String[] com = s.split("\t") ;
			
			int cluster_id = Integer.valueOf(com[0]);
			// new cluster
			if(cluster_id != pre_id){
				if(cluster_size>max_cluster_size)
					max_cluster_size = cluster_size ;
				cluster_size=0 ;
				
				total_cluster++ ;
			}

			pre_id = cluster_id ;
			cluster_size++ ;
		}
		if(cluster_size>max_cluster_size)
			max_cluster_size = cluster_size ;

		// compute k
		double k = (int)Math.ceil(Math.log(max_cluster_size)/Math.log(2)) ;

		// set Parameter
 		setCluster(total_cluster) ;
		setMaxTransSize(max_cluster_size) ;
		setPowk((int)Math.pow(2,k)) ;	
	}catch(IOException e){
		e.printStackTrace() ;
	}


	}

	public SPAM(String input){
		// default values
		setParameter(input) ;
		System.out.println("powk=" +powk + " cluster=" + cluster + " maxTransize=" + maxTransSize) ;
		// reading data
		try {
			BufferedReader in = new BufferedReader(new FileReader(input));

			String s = null ;
			int offset = 0 ; // offset for each cluster
			int pre_id = -1 ; 
			while( (s = in.readLine()) != null ){
				String[] component = s.split("\t") ;
				
				int cluster_id = Integer.valueOf(component[0]);
				String data = component[2] ;

				if(cluster_id != pre_id){ // new cluster
					offset=0 ;
				}
				int index = cluster_id*(this.powk) + offset ;

				// insert bitmap	
				String[] point = data.split(",") ;
				
				for(int i=0 ; i<point.length ; i++){
					if(!hasKeyHash(point[i])){
						addHash(point[i]) ;
						seed.add(point[i]) ;				
					}
					int[] array = getHash(point[i]) ;
					array[index]=1; 
				}
				offset++ ;	
				pre_id = cluster_id ;
			}
		}catch(IOException e){
			e.printStackTrace() ;
		}
	}

	public static void main(String[] args){
		// Deal with arguments
		String input_file=null ;
		int T = 0 ;
		for(int i=0 ; i<args.length ; i++){
			if( "-i".equals(args[i]) )
				input_file = args[++i] ;
			if( "-t".equals(args[i]) )
				T=Integer.valueOf(args[++i]) ;
		}

		// initial input
		SPAM spam = new SPAM(input_file) ;
		spam.setT(T) ;
		spam.buildTree() ;
		//spam.printHash() ;
		//spam.printOrderList() ;
		spam.printDispTree() ;

		//pause
		try{
			System.in.read();
		}catch(Exception e){};
	}		
}
