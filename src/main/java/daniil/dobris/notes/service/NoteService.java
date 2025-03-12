package daniil.dobris.notes.service;

import daniil.dobris.notes.entities.Note;
import daniil.dobris.notes.entities.User;
import daniil.dobris.notes.repository.NoteRepository;
import daniil.dobris.notes.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean createNote(Long userId, String title, String content) {
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            note.setUser(userOptional.get());
        } else {
            return false;
        }
        noteRepository.save(note);
        return true;
    }
    public List<Note> getNotesByUserId(Long userId) {
        return noteRepository.findByUserId(userId);
    }
    @Transactional
    public Note getNoteById(Long noteId) {
        return noteRepository.findById(noteId).orElse(null);
    }
    @Transactional
    public boolean updateNote(Long noteId, String title, String content) {
        Note note = getNoteById(noteId);
        if (note != null) {
            note.setTitle(title);
            note.setContent(content);
            note.setUpdatedAt(LocalDateTime.now());
            noteRepository.save(note);
            return true;
        } else {
            return false;
        }
    }
    @Transactional
    public void deleteNote(Long noteId) {
        noteRepository.deleteById(noteId);
    }
}
