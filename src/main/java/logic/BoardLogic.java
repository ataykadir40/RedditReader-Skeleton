/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.BoardDAL;
import entity.Board;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.ObjIntConsumer;

/**
 *
 * @author abdulkadir
 */
public class BoardLogic extends GenericLogic<Board, BoardDAL> {
    
    public static final String ID = "id";
    public static final String URL = "url";
    public static final String NAME = "name";
    public static final String HOST_ID = "hostId";
    
    BoardLogic(){
        super(new BoardDAL());
    }
    
    @Override
    public List<Board> getAll(){
        return get(() -> dal().findAll());
    }
    
    @Override
    public Board getWithId(int id){
        return get(() -> dal().findById(id));
    }
    
    public List<Board> getBoardsWithHostID(int hostId){
        return get(() -> dal().findByHostId(hostId));
    }
    
    public List<Board> getBoardsWithName(String name){
        return get(() -> dal().findByName(name));
    }
    
    @Override
    public Board createEntity(Map<String, String[]> parameterMap) {
        Board entity = new Board();
        
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
        //int hostId = Integer.parseInt(parameterMap.get(HOST_ID)[0]);
                 
        validator.accept(url, 45);
        validator.accept(name, 45);
        //validator.accept(host_id, 45);
                    
        entity.setUrl(url);
        entity.setName(name);
        // entity.setHostid(new Host(hostId));

        return entity;        
    } 
    
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "HostId", "Url", "Name");
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, HOST_ID, URL, NAME);
    }

    @Override
    public List<?> extractDataAsList(Board e) {
        return Arrays.asList(e.getId(), e.getUrl(), e.getHostid(), e.getName());
    }    
    
    public Board getBoardWithUrl(String url){
        return get(() -> dal().findByUrl(url));
    }
}
