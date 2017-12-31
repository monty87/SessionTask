package verto.sessiontask
import net.liftweb.json._
import net.liftweb.json.Serialization.write
import scala.io.Source

/**
 * The SessionTask program implements an application that reads a json file with events and 
 * finds a session of maximal weight, and prints its weight and duration.
 * 
 * @author  Monty Khurana
 * @version 1.0
 * @since   2017-12-29
 * 
 * 
 * 
 * Assumptions:
 * 1) Entries with parent_id= -1 are being treated as Parent events
 * 2) Entries having some value in parent_Id are considered as child events
 * 3) Cyclic inheritance is not in scope of this development.
 * 4) A session is a collection of events having parent-child relation i.e connected components
 * 5) The interruption duration is not in scope of this development
 * 6) Duration of the session should be equal to the time-stamp of starting event of the session - time-stamp of last event of the session. 
 * But in this implementation, only the largest elapsed time compared by calculating time lapse for each event 
 * 
 * 
 */


/** Case class for mapping events to and from  */
case class Events(timestamp: String, article_id: String, parent_id: String, category: String)

object SessionTask {

  /**
   * This function converts listOfEvents to Map of each event's article_Id/parent_id as key and its occurrence count as value
   *
   * @param listOfEvents This is the only parameter to getEventOccurenceCountMap
   * @return eventsWithOccurence This is the output param of type Map[String, Int]  
   */
  def getEventOccurenceCountMap(listOfEvents: List[Events]): Map[String, Int] = {
    var eventsWithOccurence = Map[String, Int]()
    for (events <- listOfEvents) {
      events.parent_id match {
        case "-1" => {
          eventsWithOccurence += (events.article_id -> 1)
        }
        case _ => {
          eventsWithOccurence.exists(x => x._1 == events.parent_id) match {
            case true => {
              eventsWithOccurence += (events.parent_id -> (eventsWithOccurence(events.parent_id) + 1))
              eventsWithOccurence += (events.article_id -> (eventsWithOccurence.getOrElse(events.article_id, 0) + 1))
            }
            case false => {
              eventsWithOccurence += (events.article_id -> 1)
            }
          }
        }
      }
    }
    eventsWithOccurence
  }

  /**
   * This function provides a Map of sessionMaxWeight for all the events provided in eventOccurenceCountMap (only for size>1)
   *
   * @param listOfEvents This is the first parameter to getEventsWithSessionWeightsMap which is the list of all the events 
	 * @param eventOccurenceCountMap This is the second parameter to getEventsWithSessionWeightsMap which is a 
	 * Map of each event's article_Id/parent_id as key and its occurrence count as value  
   * @return eventsWithSessionWeights This is the output param of type Map[String, Int] which returns a
   * Map of sessionMaxWeight for all the events provided in eventoccurrenceCountMap(size>1)
   */
  def getEventsWithSessionWeightsMap(listOfEvents: List[Events], eventOccurenceCountMap: Map[String, Int]): Map[String, Int] = {
    var eventsWithSessionWeights = Map[String, Int]()

    eventOccurenceCountMap.filter(r => (r._2 > 1)) foreach {
      case (key, value) => {
        val sessionList = listOfEvents.filter(event => event.article_id.equalsIgnoreCase(key) || event.parent_id.equalsIgnoreCase(key))
        val splittedSession = sessionList.splitAt(1)
        val parentCategory = splittedSession._1.head.category
        var count = 1
        def incrmnt(y: Int) = { count += y; count }

        // here we have session weight of this particular session
        // session weight is the maximum weight of all transitions in a session (or 0).
        val transitionList = splittedSession._2.map(childEvents => {
          if (childEvents.category.equalsIgnoreCase(parentCategory)) {
            (childEvents.category, incrmnt(1))
          } else (childEvents.category, count)
        }).filter(childCategory => !childCategory._1.equalsIgnoreCase(parentCategory))

        val sessionWeight = transitionList match {
          case Nil => (key -> -1)
          case _   => (key -> transitionList.reduce((k, v) => if (k._2 > v._2) k else v)._2)
        }
        eventsWithSessionWeights += sessionWeight
      }
    }
    eventsWithSessionWeights
  }

 /**
 * This function is used to get the Session Duration
 *
 * @param listOfEvents This is the first parameter to getSessionDuration function which is the list of all the TimeStamps for a session 
 * @return sessionDuration This is the output param of type int which returns the largest elapsed time between any two events of that session
 * 
 */
  def getSessionDuration (listOfTimestamps:List[Int]) = {
    var timeLapseList = scala.collection.mutable.MutableList[Int]()
    for (i <- 0 to listOfTimestamps.size-2){ 
       val lapseTime= listOfTimestamps(i+1) - listOfTimestamps(i)
       timeLapseList.+=(lapseTime)
      }
    timeLapseList.reduceLeft(_ max _)
  }
  
/**
 * This function is used to print the output
 *
 * @param listOfEvents This is the first parameter to printOutput which is the list of all the events 
 * @param sessionWithMaxWeight This is the second parameter to printOutput 
 * which is used for filtering the session from list of events
 */
  def printOutput(listOfEvents: List[Events], sessionWithMaxWeight: (String, Int))(implicit formats: DefaultFormats) = {
    val responseSession = listOfEvents.filter(event => event.article_id.equalsIgnoreCase(sessionWithMaxWeight._1)
      || event.parent_id.equalsIgnoreCase(sessionWithMaxWeight._1))
      
    val listOfTimestamps=responseSession.map(evnt => evnt.timestamp.toInt)
    val duration= getSessionDuration (listOfTimestamps)
    
    println("Session of Maximal Weight is :- ")
    for (session <- responseSession) {
      val jsonString = write(session)
      println(jsonString)
    }
    println("Session Weight is " + sessionWithMaxWeight._2)
    println("Session Duration is "+duration+"ms")

  }

  /**
   * This is the main method which makes use of getEventOccurenceCountMap, getEventsWithSessionWeightsMap and printOutput functions
   * @param args used for taking input file path from user.
   * @return print the session of maximal weight, its weight and duration.
   * @exception Exception.
   */
  def main(args: Array[String]) {
    println("Please Enter File path till file name here: ")
    val input = scala.io.StdIn.readLine()
    println("Processing the file ..... ")

    val bufferedSource = Source.fromFile(input)
    // implement try catch finally
    try {
      implicit val formats = DefaultFormats
      val listOfEvents = bufferedSource.getLines.toList.map(f => parse(f).extract[Events])
      val eventOccurenceCountMap = getEventOccurenceCountMap(listOfEvents)
      val mapEventsWithSessionWeight = getEventsWithSessionWeightsMap(listOfEvents, eventOccurenceCountMap)

      // finding the session of maximal weight from all sessions
      val sessionWithMaxWeight = mapEventsWithSessionWeight.maxBy { case (key, value) => value }
      printOutput(listOfEvents, sessionWithMaxWeight)
    } catch {
      case e: Exception => e.printStackTrace
    } finally {
      bufferedSource.close
    }
  }
}
