import java.util.Scanner;
import java.sql.*;

class Chatbot {
  private static String userChat, botChat, keyword;
  private static float total = 46030908; //sum of the frequency in ngram
  private static String defaultReply= "Sorry, I cannot understand.";
   //P(A and B) = P(A)P(B|A). 
  //P(B|A)= probability of B given A.

  //temporary variable
  private static int temp;
  private static String tempWord,tempWord2,tempWord3;
  private static float temp2;
  
  public static void main(String[] args){
    Scanner sc = new Scanner(System.in);
    do{
    userInput(sc);
    findKeyword();
    botChat="";
    //System.out.println(keyword);
    sentenceAdd(keyword);
    tempWord2=keyword;
    //database connection
    try{
      Class.forName("com.mysql.jdbc.Driver");
      Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/ngramdatabase","root","Tandolphin98");
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery("select* from ngramtable");
      for(int i=0;i<5;i++){ //limit the response to max 10 
        temp = 0;
        temp2 =0;
        tempWord ="";
        tempWord2="";
        tempWord3="";
        while(rs.next()){
          //sum of keyword occurance as first term p(A)=temp/total
          if(rs.getString(2).equalsIgnoreCase(keyword))
            temp+=rs.getInt(1);    
        }
        rs.absolute(1);//move the cursor back to the first row
        while(rs.next()){
 
         
          if(rs.getString(2).equalsIgnoreCase(keyword) && temp2 ==0){
            temp2=(rs.getInt(1)*temp)/total;
            tempWord=rs.getString(3);
            tempWord2=rs.getString(4);
            tempWord3=rs.getString(5);
          }
          if(rs.getString(2).equalsIgnoreCase(keyword) && ((rs.getInt(1)*temp)/total)>temp2){
            tempWord=rs.getString(3);
            tempWord2=rs.getString(4);
            tempWord3=rs.getString(5);
            temp2=(rs.getInt(1)*temp)/total;
          }
        }
        rs.absolute(1);
        keyword =tempWord;
        sentenceAdd(tempWord);
        sentenceAdd(tempWord2);
        sentenceAdd(tempWord3);
      }
      con.close();
    }catch(Exception e){
      System.out.println(e);
    }
    System.out.println("Tim: "+botChat+ ".");
    }
    while(userChat.equalsIgnoreCase("stop")==false);
  }
  //getUserInput
  public static void userInput(Scanner sc){
    System.out.print("You:");
    userChat= sc.nextLine();
  }
  
  public static void findKeyword(){
    //count how many words in user sentence
    int count =1;
    for(int i=0; i<userChat.length(); i++){
      if(userChat.charAt(i) == ' ' && i!=userChat.length()-1){
      count++;
      }
    }
    // tokenize user sentence into array 
    int temp =0;
    String[] words = new String[count];
    for(int j=0;j<userChat.length();j++){
      if(userChat.charAt(j)==' '){
      temp++;
      }
      else{
        if(words[temp]==null)
          words[temp]=Character.toString(userChat.charAt(j));
        else
          words[temp]+=userChat.charAt(j);
      }
    }
    /**
    //test
    for(int k = 0;k<words.length;k++)
      System.out.println(words[k]);
     **/
    // randomly picks a keywork
    keyword = words[(int)(Math.random()*count)]; 
  }
  //add picked words to sentence
  public static void sentenceAdd(String words){
    botChat += words +" ";
  }
}
