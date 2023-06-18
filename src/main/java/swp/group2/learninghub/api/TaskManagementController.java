package swp.group2.learninghub.api;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group2.learninghub.model.*;
import swp.group2.learninghub.model.clientModel.CardData;
import swp.group2.learninghub.model.clientModel.ColumnData;
import swp.group2.learninghub.service.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/v1/note")
public class TaskManagementController {
    @Autowired
    public NoteService noteService;
    @Autowired
    public BoardService boardService;
    @Autowired
    public BoardLabelService boardLabelService;
    @Autowired
    public ColumnService columnService;
    @Autowired
    public CardService cardService;
    @Autowired
    public CoreLabelsService coreLabelsService;
    @Autowired
    HttpSession session;
    @Autowired
    private FeatureService featureService;
    private static final int FEATURE_ID = 2;
    private static final String SUCCESSMSG = "Success";
    private static final String FAILMSG = "Fail";

    @GetMapping("test")
    public String test(){
        return"Connected";
    }

    @PostMapping("/notes")
    public ResponseEntity<ResponseObject> createNote(@RequestBody Note newNote) {
        Logger logger = Logger.getLogger(TaskManagementController.class.getName());
        try {
            Note note = noteService.createNote(newNote);
            boardService.createBoard(new Board(note.getTitle(), note.getCreatedDate(), note.getId(), true));
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(SUCCESSMSG, "Create note successfully!", note));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                    new ResponseObject(FAILMSG, "Create note failed, reason: " + e.getMessage(), null));
        }
    }
    @PostMapping("/board")
    public ResponseEntity<ResponseObject> createBoard(@RequestBody Board newBoard) {
        Logger logger = Logger.getLogger(TaskManagementController.class.getName());
        try {
            Board board = boardService.createBoard(newBoard);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(SUCCESSMSG, "Create board successfully!", board));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                    new ResponseObject(FAILMSG, "Create board failed, reason: " + e.getMessage(), null));
        }
    }
    @PostMapping("/column")
    public String createColumn(@RequestBody KanbanColumn newKanbanColumn){
        Logger logger = Logger.getLogger(TaskManagementController.class.getName());
        try{
            columnService.createNewColumn(newKanbanColumn);
            return "ok";
        }catch (Exception e){
            return e.getMessage();
        }
    }
    @GetMapping("/column")
    public ResponseEntity<ResponseObject> getColumn(@RequestParam int boardId){
        Logger logger = Logger.getLogger(TaskManagementController.class.getName());
        try{
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("success","retrieved",
                            columnService.getColumnsByBoardId(boardId)));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("fail","fail",
                            null));
        }
    }

    @GetMapping("/kanban/data")
    public Map<String, ColumnData> kanbanData(@RequestParam("boardId") int boardId){
        HashMap<String, ColumnData> result = new HashMap<>();
        ArrayList<Card> cardList = new ArrayList<>();
        ArrayList<BoardLabel> labelList = new ArrayList<>();
        ArrayList<CardData> cardData = new ArrayList<>();
        try{
            //get all column in the table
            List<KanbanColumn> kbList = columnService.getColumnsByBoardId(boardId);
            for(KanbanColumn k : kbList){
                //for retrieve all cards inside each column
                cardList = (ArrayList<Card>) cardService.getByColId(k.getId());
                //each card retrieve tags inside
                for(Card c : cardList){
                    labelList = (ArrayList<BoardLabel>) boardLabelService.getLabelsByCardId(c.getId());
                    cardData.add(new CardData(c,labelList));
                }
                result.put(k.getName(),new ColumnData(k.getName(),cardData));
            }
            return result;
        } catch (Exception e){
            return null;
        }
    }

    @GetMapping("/notes")
    public ResponseEntity<ResponseObject> showAllNotes() {
        try {
            User userSession = (User) session.getAttribute("user");
            if(userSession == null) {
                throw new IllegalArgumentException("can not find user information for this feature");
            }
            isFeatureActive();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    SUCCESSMSG, "retrieve notes of " +
                    userSession.getEmail(),
                    noteService.showUserNotesByEmail(userSession.getEmail())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                    new ResponseObject(FAILMSG, "account fail to connect: " + e.getMessage(), null));
        }
    }

    @PutMapping("/notes")

    @DeleteMapping("/notes")

    private void isFeatureActive() {
        Feature feature = featureService.findFeatureById(FEATURE_ID);
        if(!feature.isActive()) {
            throw new IllegalArgumentException("Feature is disable: " + feature.getDescription());
        }
    }
}
