package com.BdayWall.bdaywall.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.BdayWall.bdaywall.model.Video;
import com.BdayWall.bdaywall.model.Videolist;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

import static java.lang.System.exit;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.gridfs.GridFsCriteria.whereMetaData;

@RestController
public class VideoController
{
    @Autowired
    GridFsOperations gridFsOperations;
    GridFSBucket gridFSBucket;
    DB db;
    String firstname;
    String lastname;
    String birthday;
    String filename;
    String teamname;
    String gender;
    String firstname1;
    String lastname1;
    String teamname1;
    String day;
    String months;
    @Value("${app.imageDirctory}")
    String imageDirectoryPath;

    {
        System.out.println("image path is" + imageDirectoryPath);
    }

    List list = new ArrayList();
    String fileid = "";

    @Override
    public int hashCode ()
    {
        return super.hashCode();
    }

    /**Bdaywall
     * A POST request to add user input into the database. Using repository save method.
     *
     * @return a string which tells that the bday was successfully added.
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(path = "/addYourBday", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String savebday (@RequestParam(value = "file", required = false) MultipartFile multipartFile,
            @ModelAttribute Video bday) throws IOException, FileNotFoundException
    {
        boolean associateDetailsAlreadyPresent = false;
        List<String> assocID = null;
        //check if associate id already present
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        if (mongoClient != null)
        {
            DB db = mongoClient.getDB("videoWall");
            DBCollection collection = db.getCollection("fs.files");

            DBCursor obj = collection.find();
            assocID = new ArrayList<String>();
            while (obj.hasNext())
            {
                BasicDBObject Videolist = ((BasicDBObject) obj.next().get("metadata"));
                if (Videolist != null)
                    assocID.add(Videolist.get("associd").toString());
            }
        }
        for (String str: assocID) {
            System.out.println(str);
        }
        if (!assocID.isEmpty() && assocID.contains(bday.getAssocid()))
        {
            return "Associate Details already present";
        }
        else
        {
            File fileToSave = null;
            File destFile = null;
            String originalFileName = null;
            DBObject metaData = new BasicDBObject();
            metaData.put("firstname", bday.getFirstname());
            metaData.put("lastname", bday.getLastname());
            metaData.put("teamname", bday.getTeamname());
            metaData.put("gender", bday.getGender());
            metaData.put("associd", bday.getAssocid());
            metaData.put("day", bday.getDay());
            metaData.put("months", bday.getMonths());
            metaData.put("year", bday.getYear());
            // If file is null, take default image form the folder, else take the image provided by the user.
            // If file is null, take the default image and rename is to Associate ID.jpg.
            if (multipartFile == null)
            {
                //Reading image from jar file
                File defaultFile = null;
                InputStream defaultFileIS = null;
                defaultFileIS = getClass().getResourceAsStream("/pictures/cropped_giftBox6.jpg");
                //defaultFile = new File(getClass().getResource("/pictures/cropped_giftBox6.jpg").toURI());
            /*Resource resource = null;
            resource = new ClassPathResource("pictures/cropped_giftBox6.jpg");
            resource.getInputStream();
            File defaultFile = resource.getFile();*/
                destFile = new File(bday.getAssocid() + ".jpg");
                FileUtils.copyToFile(defaultFileIS, destFile);
                metaData.put("filename", destFile.getName());
                originalFileName = destFile.getName();
            }
            else
            {
                metaData.put("filename", multipartFile.getOriginalFilename());
                originalFileName = multipartFile.getOriginalFilename();
            }
            File f = new File(imageDirectoryPath);
            System.out.println("Absolute path is " + f.getAbsolutePath());
            if (f.isDirectory())
            {
                System.out.println("The path directory");
            }
            if (destFile != null)
            {
                fileToSave = new File(imageDirectoryPath + File.separator + destFile.getName());
                FileUtils.copyFile(destFile, fileToSave);
                destFile.delete();
            }
            else
            {
                fileToSave = new File(imageDirectoryPath + File.separator + multipartFile.getOriginalFilename());
                multipartFile.transferTo(fileToSave);
            }
            System.out.println("Image file Absolute path is " + fileToSave.getAbsolutePath());
            // copy file content from received file to new local file
            InputStream inputStream = new FileInputStream(fileToSave);
            metaData.put("type", "image");
            fileid = gridFsOperations.store(inputStream, originalFileName, metaData).toString();
            return "Successfully added your birthday!";
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(path = "/deleteYourBday", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String deletebday (@PathVariable("months") String associd)
    {
        try
        {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            if (mongoClient != null)
            {
                DB db = mongoClient.getDB("videoWall");
                GridFS gridFS = new GridFS(db);
                BasicDBObject query = new BasicDBObject();
                query.put("metadata.associd", associd);
                gridFS.remove(query);
            }
        } catch (Exception e)
        {
            System.out.println(e.getCause());
        }
        return "Deleted successfully!";
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/viewBday")
    public List<Videolist> viewBday ()
    {
        List<Videolist> hdxList = new ArrayList<>();
        List<Videolist> himList = new ArrayList<>();
        List<Videolist> ccmList = new ArrayList<>();
        List<Videolist> schedulingList = new ArrayList<>();
        List<Videolist> analyticsList = new ArrayList<>();
        List<Videolist> cpaList = new ArrayList<>();
        List<Videolist> registrationList = new ArrayList<>();

        try
        {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            if (mongoClient != null)
            {
                DB db = mongoClient.getDB("videoWall");
                DBCollection collection = db.getCollection("fs.files");
                GridFS gridFS = new GridFS(db);
                DBCursor dbCursor = collection.find();
                while (dbCursor.hasNext())
                {
                    Videolist Videolist = new Videolist();
                    BasicDBObject basicDBObject = ((BasicDBObject) dbCursor.next().get("metadata"));
                    Videolist.setFirstname(basicDBObject.get("firstname").toString());
                    Videolist.setLastname(basicDBObject.get("lastname").toString());
                    Videolist.setTeamname(basicDBObject.get("teamname").toString());
                    Videolist.setAssocid(basicDBObject.get("associd").toString());
                    Videolist.setGender(basicDBObject.get("gender").toString());
                    Videolist.setFilename(basicDBObject.get("filename").toString());
                    Videolist.setType(basicDBObject.get("type").toString());
                    Videolist.setDay(Integer.parseInt(basicDBObject.get("day").toString()));
                    Videolist.setMonths(basicDBObject.get("months").toString());
                    Videolist.setYear(Integer.parseInt(basicDBObject.get("year").toString()));

                    switch (Videolist.getTeamname().toUpperCase())
                    {
                        case "HDXTS":
                            hdxList.add(Videolist);
                            break;
                        case "HIM":
                            himList.add(Videolist);
                            break;
                        case "CCM":
                            ccmList.add(Videolist);
                            break;
                        case "CPA":
                            cpaList.add(Videolist);
                            break;
                        case "ANALYTICS":
                            analyticsList.add(Videolist);
                            break;
                        case "SCHEDULING":
                            schedulingList.add(Videolist);
                            break;
                        case "REGISTRATION":
                            registrationList.add(Videolist);
                            break;
                    }
                }
            }
        } catch (Exception e)
        {
            System.out.println(e.getCause());
        }
        //return "Successfully retrieved!";
        //return ccmList;
        return ccmList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/viewAllBdayInMap")
    public Map viewAllBdayInMap ()
    {
        List<Videolist> hdxList = new ArrayList<>();
        List<Videolist> himList = new ArrayList<>();
        List<Videolist> ccmList = new ArrayList<>();
        List<Videolist> schedulingList = new ArrayList<>();
        List<Videolist> analyticsList = new ArrayList<>();
        List<Videolist> cpaList = new ArrayList<>();
        List<Videolist> registrationList = new ArrayList<>();
        Map bdayMap = new HashMap<>();

        try
        {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            if (mongoClient != null)
            {
                DB db = mongoClient.getDB("videoWall");
                DBCollection collection = db.getCollection("fs.files");
                //GridFS gridFS = new GridFS(db);
                DBCursor dbCursor = collection.find();
                while (dbCursor.hasNext())
                {
                    Videolist Videolist = new Videolist();
                    BasicDBObject basicDBObject = ((BasicDBObject) dbCursor.next().get("metadata"));
                    Videolist.setFirstname(basicDBObject.get("firstname").toString());
                    Videolist.setLastname(basicDBObject.get("lastname").toString());
                    Videolist.setTeamname(basicDBObject.get("teamname").toString());
                    Videolist.setAssocid(basicDBObject.get("associd").toString());
                    Videolist.setGender(basicDBObject.get("gender").toString());
                    Videolist.setFilename(basicDBObject.get("filename").toString());
                    Videolist.setType(basicDBObject.get("type").toString());
                    Videolist.setDay(Integer.parseInt(basicDBObject.get("day").toString()));
                    Videolist.setMonths(basicDBObject.get("months").toString());
                    Videolist.setYear(Integer.parseInt(basicDBObject.get("year").toString()));

                    switch (Videolist.getTeamname().toUpperCase())
                    {
                        case "HDXTS":
                            hdxList.add(Videolist);
                            break;
                        case "HIM":
                            himList.add(Videolist);
                            break;
                        case "CCM":
                            ccmList.add(Videolist);
                            break;
                        case "CPA":
                            cpaList.add(Videolist);
                            break;
                        case "ANALYTICS":
                            analyticsList.add(Videolist);
                            break;
                        case "SCHEDULING":
                            schedulingList.add(Videolist);
                            break;
                        case "REGISTRATION":
                            registrationList.add(Videolist);
                            break;
                    }
                }
            }
            bdayMap.put("hdxList", hdxList);
            bdayMap.put("himList", himList);
            bdayMap.put("ccmList", ccmList);
            bdayMap.put("cpaList", cpaList);
            bdayMap.put("schedulingList", schedulingList);
            bdayMap.put("registrationList", registrationList);
            bdayMap.put("analyticsList", analyticsList);
        } catch (Exception e)
        {
            System.out.println(e.getCause());
        }
        //return "Successfully retrieved!";
        return bdayMap;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/viewonlySelectedList")
    public List<Videolist> viewonlySelectedList (String teamName)
    {
        List<Videolist> selectedTeamList = new ArrayList<>();
        try
        {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            teamName = "cpa";
            if (mongoClient != null)
            {
                DB db = mongoClient.getDB("videoWall");
                DBCollection collection = db.getCollection("fs.files");
                DBCursor dbCursor = collection.find();
                while (dbCursor.hasNext()) {
                    Videolist Videolist = new Videolist();
                    BasicDBObject basicDBObject = ((BasicDBObject) dbCursor.next().get("metadata"));
                    if(basicDBObject.get("teamname").toString().equalsIgnoreCase(teamName))
                    {
                        Videolist.setFirstname(basicDBObject.get("firstname").toString());
                        Videolist.setLastname(basicDBObject.get("lastname").toString());
                        Videolist.setTeamname(basicDBObject.get("teamname").toString());
                        Videolist.setAssocid(basicDBObject.get("associd").toString());
                        Videolist.setGender(basicDBObject.get("gender").toString());
                        Videolist.setFilename(basicDBObject.get("filename").toString());
                        Videolist.setType(basicDBObject.get("type").toString());
                        Videolist.setDay(Integer.parseInt(basicDBObject.get("day").toString()));
                        Videolist.setMonths(basicDBObject.get("months").toString());
                        Videolist.setYear(Integer.parseInt(basicDBObject.get("year").toString()));
                        selectedTeamList.add(Videolist);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
        return selectedTeamList;
    }


    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/todaysBday/{day}/{months}")
    public List<Object> getByDayAndMonths (@PathVariable("day") Integer day, @PathVariable("months") String months)
            throws IOException
    {
        GridFSFindIterable imageFile = gridFsOperations.find(
                query(whereMetaData("day").is(day).andOperator(Criteria.where("metadata.months").is(months))));
        List<Object> bday = new ArrayList<Object>();
        imageFile.forEach(new Block<GridFSFile>()
        {
            @Override
            public void apply (GridFSFile gridFSFile)
            {
                Videolist blist = new Videolist();
                firstname = gridFSFile.getMetadata().get("firstname").toString();
                lastname = gridFSFile.getMetadata().get("lastname").toString();
                teamname = gridFSFile.getMetadata().get("teamname").toString();
                gender = gridFSFile.getMetadata().get("gender").toString();
                System.out.println(teamname);
                firstname1 = firstname.toUpperCase();
                lastname1 = lastname.toUpperCase();
                teamname1 = teamname.toUpperCase();
                filename = gridFSFile.getFilename();
                blist.setFilename(filename);
                blist.setFirstname(firstname1);
                blist.setLastname(lastname1);
                blist.setTeamname(teamname1);
                blist.setGender(gender);
                bday.add(blist);
            }
        });
        return bday;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/unique/{associd}")
    public String getByAssocid (@PathVariable("associd") String associd) throws NullPointerException
    {
        try
        {
            GridFSFile File = gridFsOperations.findOne(query(whereMetaData("associd").is(associd)));
            File.equals("");
            System.out.println(File);
            String id1 = File.getMetadata().get("associd").toString();
            System.out.println(associd.getClass());
            System.out.println(id1.getClass());
            Boolean b = id1.equals(associd);
            System.out.println(b);
            return "true";
        } catch (NullPointerException e)
        {
            System.out.print("false");
            return "false";
        }
    }
}