package daniil.dobris.notes.controllers;

import daniil.dobris.notes.entities.Note;
import daniil.dobris.notes.entities.User;
import daniil.dobris.notes.service.NoteService;
import daniil.dobris.notes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notes")
public class NoteController {
    @Autowired
    private NoteService noteService;
    @Autowired
    private UserService userService;
    /*@Autowired
    private SecurityService securityService;*/

    @GetMapping
    public String showNotesPage(@RequestParam("userId") Long userId, Model model) {
        List<Note> notes = noteService.getNotesByUserId(userId);
        User curUser = userService.findUserById(userId);
        model.addAttribute("user", curUser);
        model.addAttribute("notes", notes);
        model.addAttribute("userId", userId);
        return "notes";
    }
    @GetMapping("/create")
    public String showCreateNotePage(@RequestParam("userId") Long userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "login";
        }
        /*Long currentUserId = securityService.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            model.addAttribute("error", "You are not authorized to edit this note");
            return "redirect:/notes";
        }*/
        model.addAttribute("user", user);
        return "create_note";
    }
    @PostMapping("/create")
    public String createNote(@RequestParam("userId") Long userId,
                             @RequestParam("title") String title, @RequestParam("content") String content, Model model) {
        /*Long currentUserId = securityService.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            model.addAttribute("error", "You are not authorized to edit this note");
            return "redirect:/notes";
        }*/
        if (!noteService.createNote(userId, title, content)) {
            model.addAttribute("message", "This user is not exist");
            return "redirect:/notes";
        }
        return "redirect:/notes?userId=" + userId;
    }
    @GetMapping("/edit/{id}")
    public String showEditNoteForm(@PathVariable("id") Long id, Model model) {
        Note note = noteService.getNoteById(id);
        if (note != null) {
            /*Long currentUserId = securityService.getCurrentUserId();
            if (!currentUserId.equals(note.getUser().getId())) {
                model.addAttribute("error", "You are not authorized to edit this note");
                return "redirect:/notes";
            }*/
            model.addAttribute("note", note);
            model.addAttribute("userId", note.getUser().getId());
            return "edit_note";
        } else {
            model.addAttribute("error", "Note has not found");
            return "redirect:/notes";
        }
    }
    @PostMapping("/edit/{id}")
    public String editNote(@PathVariable("id") Long id,
                           @RequestParam("title") String title, @RequestParam("content") String content,
                           @RequestParam("userId") Long userId, Model model) {
        /*Long currentUserId = securityService.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            model.addAttribute("error", "You are not authorized to edit this note");
            return "redirect:/notes";
        }*/
        if (content == null || content.trim().isEmpty()) {
            noteService.deleteNote(id);
            return "redirect:/notes?userId=" + userId;
        }
        if (noteService.updateNote(id, title, content)) {
            return "redirect:/notes?userId=" + userId;
        } else {
            model.addAttribute("error", "This note has not found");
            return "redirect:/notes/edit/{" + id + "}";
        }
    }
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public String deleteNode(@PathVariable("id") Long id, @RequestParam("userId") Long userId) {
        noteService.deleteNote(id);
        return "redirect:/notes?userId=" + userId;
    }
}
