import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Lab2 extends JFrame implements ActionListener {
	JButton open = new JButton("Next Program");
	JTextArea result = new JTextArea(20,40);
	JLabel errors = new JLabel();
	JScrollPane scroller = new JScrollPane();
	
	public Lab2() {
		setLayout(new java.awt.FlowLayout());
		setSize(500,430);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		add(open); open.addActionListener(this);
		scroller.getViewport().add(result);
		add(scroller);
		add(errors);
	}
	
	public void actionPerformed(ActionEvent evt) {
		result.setText("");	//clear TextArea for next program
		errors.setText("");
		processProgram();
	}
	
	public static void main(String[] args) {
		Lab2 display = new Lab2();
		display.setVisible(true);
	}
	
	String getFileName() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile().getPath();
		else
			return null;
	}
	
/************************************************************************/
/* Put your implementation of the processProgram method here.           */
/* Use the getFileName method to allow the user to select a program.    */
/* Then simulate the execution of that program.                         */
/* You may add any other methods that you think are appropriate.        */
/* However, you should not change anything in the code that I have      */
/* written.                                                             */
/************************************************************************/
	//NAMING
	ArrayList<String> lines;
	int lineNumber;
	HashMap<String,Double> storage = new HashMap<>();
	boolean end ;
	void processProgram() {
	 lines = new ArrayList<String>();
	 lineNumber= 1;
	 end = true;
	 
	  String fileName = getFileName();
	  BufferedReader reader;
	   
	try {
		reader = new BufferedReader(new FileReader(fileName));
		 lines.add(""); //index 0
		 String line ="";
		  while((line = reader.readLine())!=null) {// adding the lines to array list
			 lines.add(line); 
		  }
		  reader.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	while(end) {
		String s1 = lines.get(lineNumber);
		lineNumber++;
		if (s1.equals("END")){ 
			end=false;
			continue;
		}
		// if there are statements after End.
		if (lineNumber<(lines.size()-1) && lines.get(lineNumber).equals("END")) {
			errors.setText("There are statements following END");
			end=false;
			result.setText("");
		}
		if(lines.get(lines.size()-1).equals("END")) {
		doStatement(s1);}
		else {
			errors.setText("END is not in the last line");
			end=false;
			result.setText("");	
		}
	}
	
  }
  void doStatement(String instruction) {
	  if (instruction.isEmpty()) { //if the line is empty
		errors.setText("Empty line");  
		end = false;
		result.setText("");
		return;		
	  }
	  StringTokenizer st = new StringTokenizer(instruction,"  ");
	  ArrayList<String> eachWord = new ArrayList<String>();
	  while (st.hasMoreTokens()) {
	  String word = st.nextToken();
	  eachWord.add(word);
	  } 
	  String firstElement = eachWord.get(0);
	  // detecting the instruction to be executed
	  if(firstElement.equals("GOTO")) {
		  goTo(eachWord);
	  }
	  else if(firstElement.equals("PRINT")) {
		  printVariable(eachWord);
	  }
	  else if(firstElement.equals("IF")) {
		  conditionalStatement(eachWord);
	  }
	  else {
		  evaluateExpression(eachWord);
	  }  
  }
  // creating all the methods for the instructions
   void goTo(ArrayList<String> statement) {
	  try {
	  int line = Integer.parseInt(statement.get(1));
	  ;
	  if (0 < line && line < lines.size()-1) {
	  lineNumber = line;}
	  else {
		errors.setText("Invalid Line Number in line "+ (lineNumber-1));  
		end = false;
		
	  }
	  }
	  catch (NumberFormatException e) {
		  errors.setText("Invalid token goto");
		  result.setText("");
		  return;
	  }
   }
   void printVariable(ArrayList<String> statement2) {
	   
	   String variable1 = statement2.get(1);
	   if(storage.containsKey(variable1)) {   //if else to check whether the variable is in the hash map or not
	   double value1 = storage.get(variable1);
	   String decimal1 = String.format("%.2f",value1);
	   result.append(decimal1 +"\n");} //appends uses just strings  
	   else {
		   errors.setText("In line "+(lineNumber-1)+" \"" +variable1+ "\""+" is undefined");
		   end=false;
		   result.setText("");
	   }
	   
   }
   void conditionalStatement(ArrayList<String> statement3) {
	   String ifKeyword = statement3.get(0);
	   String isKeyword = statement3.get(2);
	   String thenKeyword = statement3.get(4);
	   if(!ifKeyword.equals("IF")||!isKeyword.equals("IS")||!thenKeyword.equals("THEN")) {
		   errors.setText("conditional statement  is invalid");
		   end=false;
		   result.setText("");
	   }
	   String second = statement3.get(1);
	   double secondElement = storage.get(second);
	   double value = 0;
	   if(storage.containsKey(statement3.get(3))) {
	       value = storage.get(statement3.get(3));
	   }
	   else {
		   value = Double.parseDouble(statement3.get(3));
	   }
	   
	   String simpleStatement = statement3.get(5);
	   
	   for(int i =0;i<5;i++) {
		   statement3.remove(0);
		   }
	   
	   if(secondElement== value) {
		   if (simpleStatement.equals("GOTO")) {
			   goTo(statement3);
		   }
		   else if(simpleStatement.equals("PRINT")) {
			   printVariable(statement3);
		   }
		   else {// evaluate expression
			   evaluateExpression(statement3);
		   }
	 }  
   }
   void evaluateExpression(ArrayList<String> statement4) {
	   if(3>statement4.size()) {
		errors.setText("Invalid line Format");
		end = false;
		result.setText("");
		return;
	   }
	   // variable = value (instance of one)
	   // variable = expression (instance when we have more such as a+5*s)
	   String assignmentOperator = statement4.get(1);
	   if (!assignmentOperator.equals("=")) {
		   errors.setText("Assignment opertor missing");
			end = false;
			result.setText("");
			return; 
	   }
	   String firstWord = statement4.get(0);
	   String secondWord = statement4.get(2);
	   

	  
	   double variable2 = 0.0;  
	   if (!storage.containsKey(secondWord)) {
		try {
		  variable2 = Double.parseDouble(secondWord);
		   	}
		catch(NumberFormatException e) {
			errors.setText("Invalid token 1");
			end = false;
			result.setText("");
			return;
			}  
		catch(NullPointerException e) {
			errors.setText("Invalid token");
			result.setText("");
			end = false;
			return;
		}
		
	   }  
	   else {  //if the hash map contains the variable that is being assigned to the first word
			 variable2 = storage.get(secondWord);
			  }
//		       EXAMPLE:
//			   a = b + 2 - 5 * c / 1  
//			   0 1 2 3 4 5 6 7 8 9 10  ----> indexes
			  int i = 0;
			  double totalValue = variable2;
			  for(i=3;i<statement4.size()-1;i=i+2) { 
				 String operator = statement4.get(i);
				 String operands = statement4.get(i+1);
				 if (storage.containsKey(operands)) { //if hash map has the key
					 Double valueOfOperand = storage.get(operands);
					 if (operator.equals("+")||operator.equals("-")||operator.equals("*")||operator.equals("/")) {
					 if (operator.equals("+")) {
						 totalValue = totalValue + valueOfOperand;
					 }
					 else if(operator.equals("-")) {
						 totalValue = totalValue - valueOfOperand;
					 }
					 else if(operator.equals("*")) {
						 totalValue = totalValue * valueOfOperand;
					 }	
					 else {
						 totalValue = totalValue / valueOfOperand;
					 }}
					 else {
						 errors.setText("THERE IS AN INVALID OPERATOR IN LINE " + (lineNumber-1));
							result.setText("");
							end = false;
							return; 
					 }
				 }
				 else { //if hash map doesn't contain key
					 try {
					 Double valueOfOperand = Double.parseDouble(operands);
					 if (operator.equals("+")||operator.equals("-")||operator.equals("*")||operator.equals("/")) {
					 if (operator.equals("+")) {
						 totalValue = totalValue + valueOfOperand;
					 }
					 else if(operator.equals("-")) {
						 totalValue = totalValue - valueOfOperand;
					 }
					 else if(operator.equals("*")) {
						 totalValue = totalValue * valueOfOperand;
					 }	
					 else {
						 totalValue = totalValue/ valueOfOperand;
					 }
					 }
					 else {
						 errors.setText("THERE IS AN INVALID OPERATOR IN LINE "+ (lineNumber-1));
							result.setText("");
							end = false;
							return;  
					 }}
					 catch(NumberFormatException e) {
						 errors.setText("Invalid Token #2");
						 end = false;
						 result.setText("");
						 return;
					 }
					 
				 }
			  }
	 storage.put(firstWord,totalValue);
   } 
}







