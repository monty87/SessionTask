package verto.sessiontask

object Task {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(84); 
  println("Welcome to the Scala worksheet");$skip(34); 
  
  val monty= List(10,12,13,15);System.out.println("""monty  : List[Int] = """ + $show(monty ));$skip(25); 
  
  val l= monty.length;System.out.println("""l  : Int = """ + $show(l ));$skip(23); 
  
  val s= monty.size;System.out.println("""s  : Int = """ + $show(s ))}
  
}
