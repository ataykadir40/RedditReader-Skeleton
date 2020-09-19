/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Board;
import entity.Image;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author abdulkadir
 */
public class ImageLogicTest {
   
    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private ImageLogic logic;
    private Image expectedImage;
    private Board board = new Board(1);

    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat("/RedditReader", "common.ServletListener");
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }
    
    @BeforeEach
    final void setUp() throws Exception {
        Image image = new Image();
        image.setDate(FORMATTER.parse("2020-06-15 12:00:00"));
        image.setLocalPath("C:\\helloworld");
        image.setTitle("junit5");
        image.setUrl("http:\\www.algonquin.com");
        image.setBoard(board);
        image.setId(12);

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction 
        em.getTransaction().begin();
        //add an image to hibernate, image is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedImage = em.merge(image);
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();

        logic = LogicFactory.getFor( "Image");
    }
    
    @AfterEach
    final void tearDown() throws Exception {
        if (expectedImage != null) {
            logic.delete(expectedImage);
        }
    }
    
    private void assertImageEquals(Image  expected, Image  actual) {
        //assert all field to guarantee they are the same
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getLocalPath(), actual.getLocalPath());
        assertEquals(expected.getBoard().getId(), actual.getBoard().getId());
    }
    
    @Test
    final void testGetAll() {
        List<Image> list = logic.getAll();
         int originalSize = list.size();
         assertNotNull(expectedImage);
          logic.delete(expectedImage);
          
          list = logic.getAll();
          assertEquals(originalSize - 1, list.size());
    }
    
    @Test
    final void testGetWithId() {
        Image returnedImage = logic.getWithId(expectedImage.getId());
       
        assertImageEquals(expectedImage, returnedImage);
    }
    
    @Test
    final void testGetImagesWithBoardId() {
        List<Image> returnedImage = logic.getImagesWithBoardId(expectedImage.getBoard().getId());
       
        for(int i=0; i<returnedImage.size(); i++) {
            assertEquals(returnedImage.get(i).getBoard().getId(), expectedImage.getBoard().getId());
        }
    }
    
    @Test
    final void testGetImagesWithTitle() {
        List<Image> returnedImage = logic.getImagesWithTitle(expectedImage.getTitle());

        for(int i=0; i<returnedImage.size(); i++) {
            assertEquals(returnedImage.get(i).getTitle(), expectedImage.getTitle());
        }
    }
    
    @Test
    final void testGetImagesWithUrl() {
        Image returnedImage = logic.getImagesWithUrl(expectedImage.getUrl());

        //the two Images (testImages and returnedImages) must be the same
        assertImageEquals(expectedImage, returnedImage);
    }
    
    @Test
    final void testGetImagesWithLocalPath() {
        Image returnedImage = logic.getImagesWithLocalPath(expectedImage.getLocalPath());

        //the two Images (testImages and returnedImages) must be the same
        assertImageEquals(expectedImage, returnedImage);
    }
    
    @Test
    final void testGetImagesWithDate() {
        List<Image> returnedImage = logic.getImagesWithDate(expectedImage.getDate());

        //the two Images (testImages and returnedImages) must be the same
        for(int i=0; i<returnedImage.size(); i++) {
            assertTrue(Math.abs(returnedImage.get(i).getDate().getTime()-expectedImage.getDate().getTime())<2000);
        }
    }
    
    @Test
    final void testConvertDate() {
        try {
            Date expectedDate = FORMATTER.parse("2020-06-15 12:00:00");
            assertEquals("2020-06-15 12:00:00", logic.convertDate(expectedDate));
        } catch (ParseException ex) {
            Logger.getLogger(ImageLogicTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

     @Test
    final void testCreateEntity() { 
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
        sampleMap.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});
        sampleMap.put(ImageLogic.LOCAL_PATH, new String[]{expectedImage.getLocalPath()});
        sampleMap.put(ImageLogic.TITLE, new String[]{expectedImage.getTitle()});
        sampleMap.put(ImageLogic.DATE, new String[]{FORMATTER.format(expectedImage.getDate())});

        Image returnedImage = logic.createEntity(sampleMap);

        assertEquals(expectedImage.getId(), returnedImage.getId());
        assertEquals(expectedImage.getUrl(), returnedImage.getUrl());
        assertEquals(expectedImage.getTitle(), returnedImage.getTitle());
        assertEquals(expectedImage.getDate(), returnedImage.getDate());
        assertEquals(expectedImage.getLocalPath(), returnedImage.getLocalPath());
        
        sampleMap.replace(ImageLogic.ID, new String[]{null});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
        
        sampleMap.replace(ImageLogic.URL, new String[]{null});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.URL, new String[]{expectedImage.getUrl()});
        
        sampleMap.replace(ImageLogic.TITLE, new String[]{"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.TITLE, new String[]{expectedImage.getTitle()});
        
        sampleMap.replace(ImageLogic.DATE, new String[]{"2020/1/23"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.DATE, new String[]{FORMATTER.format(expectedImage.getDate())});

        sampleMap.replace(ImageLogic.LOCAL_PATH, new String[]{"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.LOCAL_PATH, new String[]{expectedImage.getLocalPath()});
    }
    
    @Test
    final void testGetColumnNames() {
        //List<String> list = logic.getColumnNames();
        //assertEquals(Arrays.asList("id", "Board_id", "title", "url", "local_path","date"), list);
        List<String> testList = logic.getColumnNames();
        assertEquals(testList.size(), 6);
        List<String> colNameList = Arrays.asList("id", "Board_id", "title", "url", "local_path","date");
        for(int i=0; i < testList.size(); i++)
            assertEquals(colNameList.get(i), testList.get(i));
    }
    
    @Test
    final void testGetColumnCodes() {
        //List<String> list = logic.getColumnCodes();
        //assertEquals(Arrays.asList(ImageLogic.ID, ImageLogic.BOARD_ID, ImageLogic.TITLE, ImageLogic.URL,
        //        ImageLogic.LOCAL_PATH, ImageLogic.DATE), list);
        List<String> testList = logic.getColumnCodes();
        assertEquals(testList.size(), 6);
        List<String> colCodeList = Arrays.asList(ImageLogic.ID, ImageLogic.BOARD_ID, ImageLogic.TITLE, ImageLogic.URL,
                ImageLogic.LOCAL_PATH, ImageLogic.DATE);
        for(int i=0; i < testList.size(); i++)
            assertEquals(colCodeList.get(i), testList.get(i));
    }
    
    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedImage);
        assertEquals(expectedImage.getId(), list.get(0));
        assertEquals(expectedImage.getBoard().getId(), list.get(1));
        assertEquals(expectedImage.getTitle(), list.get(2));
        assertEquals(expectedImage.getUrl(), list.get(3));
        assertEquals(expectedImage.getLocalPath(), list.get(4));
        assertEquals(expectedImage.getDate(), list.get(5));
    }
}
