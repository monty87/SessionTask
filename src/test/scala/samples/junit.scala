package samples

import org.junit._
import Assert._
import verto.sessiontask._
import net.liftweb.json._
import net.liftweb.json.Serialization.write
import scala.io.Source

@Test
class SessionTaskTest {  
  
  var jsonString= """ {"timestamp":1505379681,"article_id":1822925634,"parent_id":-1,"category":"Science"}
      {"timestamp":1505382636,"article_id":1266710134,"parent_id":-1,"category":"Games"}
      {"timestamp":1505384710,"article_id":572534000,"parent_id":1266710134,"category":"Games"}
      {"timestamp":1505385318,"article_id":755526559,"parent_id":1266710134,"category":"Games"}
      {"timestamp":1505386057,"article_id":334225414,"parent_id":1266710134,"category":"Kittens"}
      {"timestamp":1505389055,"article_id":1425900045,"parent_id":-1,"category":"Science"}
      {"timestamp":1505389219,"article_id":2051302133,"parent_id":1425900045,"category":"Politics"}
      {"timestamp":1505389955,"article_id":1672468126,"parent_id":-1,"category":"Kittens"}
      {"timestamp":1505390534,"article_id":1946737694,"parent_id":2051302133,"category":"Fashion"}
      {"timestamp":1505392564,"article_id":598653220,"parent_id":2051302133,"category":"Games"}"""
  
      @Before
      implicit val formats = DefaultFormats
      val bufferedSource = Source.fromString(jsonString)
      val listOfEvents = bufferedSource.getLines.toList.map(f => parse(f).extract[Events])
      val eventOccurenceCountMap = verto.sessiontask.SessionTask.getEventOccurenceCountMap(listOfEvents)
      val mapEventsWithSessionWeight = verto.sessiontask.SessionTask.getEventsWithSessionWeightsMap(listOfEvents, eventOccurenceCountMap)
      val sessionWithMaxWeight = mapEventsWithSessionWeight.maxBy { case (key, value) => value }   
    
  
    @Test
    def testArticleIdWithSessionMaxWeight()  {
      assertEquals("The articleId of Session with Maximal Weight should be 1266710134","1266710134",sessionWithMaxWeight._1)
    }

    @Test
     def testSessionWeight()  {     
      assertEquals("The Session Weight should be 3",3,sessionWithMaxWeight._2)
    }
    
    @Test
     def testSessionDuration()  {
      val responseSession = listOfEvents.filter(event => event.article_id.equalsIgnoreCase(sessionWithMaxWeight._1)
      || event.parent_id.equalsIgnoreCase(sessionWithMaxWeight._1))      
      val listOfTimestamps=responseSession.map(evnt => evnt.timestamp.toInt)
      val duration= verto.sessiontask.SessionTask.getSessionDuration (listOfTimestamps)
      assertEquals("The Session duration should be 2074",2074,duration)
    }


}


