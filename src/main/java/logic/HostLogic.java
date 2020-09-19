/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import java.util.List;
import dal.HostDAL;
import entity.Host;
import java.util.Arrays;
import java.util.Map;
import java.util.function.ObjIntConsumer;

/**
 *
 * @author abdulkadir
 */
public class HostLogic extends GenericLogic<Host, HostDAL> {
    
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String URL = "url";
    public static final String EXTRACTION_TYPE = "extractionType";


    HostLogic(){
        super(new HostDAL());
}
    @Override
    public List<Host> getAll(){
        return get(() -> dal().findAll());
}
    @Override
    public Host getWithId(int id){
        return get(() -> dal().findById(id)); 
}
    public Host getHostWithName(String name){
        return get(() -> dal().findByName(name));
}
    public Host getHostWithUrl(String url){
        return get(() -> dal().findByUrl(url));
}
    public List<Host> getHostWithExtractionType(String type){
        return get(() -> dal().findByExtractionType(type));
}
    @Override
    public Host createEntity(Map<String, String[]> parameterMap) {
        Host entity = new Host();
        
        if (parameterMap.containsKey(ID)) {
            try {
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }
            ObjIntConsumer< String> validator = (value, length) -> {
            if (value == null || value.trim().isEmpty() || value.length() > length) {
                throw new ValidationException("value cannot be null, empty or larger than " + length + " characters");
            }
            };
        String url = parameterMap.get(URL)[0];
        String name = parameterMap.get(NAME)[0];
        //int extractionType = Integer.parseInt(parameterMap.get(EXTRACTION_TYPE)[0]);
        String extractionType = parameterMap.get(EXTRACTION_TYPE)[0];
                    
        validator.accept(url, 45);
        validator.accept(name, 45);
        validator.accept(extractionType, 45);
                    
        entity.setUrl(url);
        entity.setName(name);
        entity.setExtractionType(extractionType);

        return entity;
        
    } 
                    
@Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "Name", "Url", "Extraction_type");
}
@Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, NAME, URL, EXTRACTION_TYPE);
}
@Override
    public List<?> extractDataAsList(Host e) {
         return Arrays.asList(e.getId(), e.getName(), e.getUrl(), e.getExtractionType());
}
}