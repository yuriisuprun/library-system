import { useState, useEffect } from 'react'
import axios from 'axios'
import './App.css'

interface Book {
  id?: number;
  title: string;
  author: string;
  isbn: string;
  publishedYear: number;
}

function App() {
  const [books, setBooks] = useState<Book[]>([])
  const [newBook, setNewBook] = useState<Book>({ title: '', author: '', isbn: '', publishedYear: 0 })
  const [searchQuery, setSearchQuery] = useState('')
  const [isSearching, setIsSearching] = useState(false)
  const [searchType, setSearchType] = useState<'all' | 'title' | 'author' | 'isbn' | 'year'>('all')

  useEffect(() => {
    fetchBooks()
  }, [])

  const fetchBooks = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/books')
      setBooks(response.data)
      setIsSearching(false)
      setSearchQuery('')
    } catch (error) {
      console.error('Error fetching books:', error)
    }
  }

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!searchQuery.trim()) {
      fetchBooks()
      return
    }

    try {
      setIsSearching(true)
      let response

      switch (searchType) {
        case 'title':
          response = await axios.get('http://localhost:8080/api/books/search/title', {
            params: { title: searchQuery }
          })
          break
        case 'author':
          response = await axios.get('http://localhost:8080/api/books/search/author', {
            params: { author: searchQuery }
          })
          break
        case 'isbn':
          response = await axios.get('http://localhost:8080/api/books/search/isbn', {
            params: { isbn: searchQuery }
          })
          break
        case 'year':
          response = await axios.get('http://localhost:8080/api/books/search/year', {
            params: { year: parseInt(searchQuery) }
          })
          break
        default: // 'all'
          response = await axios.get('http://localhost:8080/api/books/search', {
            params: { query: searchQuery }
          })
      }

      setBooks(response.data)
    } catch (error) {
      console.error('Error searching books:', error)
      setBooks([])
    }
  }

  const clearSearch = () => {
    setSearchQuery('')
    setSearchType('all')
    fetchBooks()
  }

  const addBook = async () => {
    try {
      await axios.post('http://localhost:8080/api/books', newBook)
      fetchBooks()
      setNewBook({ title: '', author: '', isbn: '', publishedYear: 0 })
    } catch (error) {
      console.error('Error adding book:', error)
    }
  }

  const deleteBook = async (id: number) => {
    try {
      await axios.delete(`http://localhost:8080/api/books/${id}`)
      fetchBooks()
    } catch (error) {
      console.error('Error deleting book:', error)
    }
  }

  return (
    <div className="App">
      <h1>📚 Library App</h1>

      {/* Search Section */}
      <div className="search-section">
        <h2>Search Books</h2>
        <form onSubmit={handleSearch} className="search-form">
          <input
            type="text"
            placeholder="Enter search query..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="search-input"
          />

          <select
            value={searchType}
            onChange={(e) => setSearchType(e.target.value as any)}
            className="search-type-select"
          >
            <option value="all">Search All Fields</option>
            <option value="title">Search by Title</option>
            <option value="author">Search by Author</option>
            <option value="isbn">Search by ISBN</option>
            <option value="year">Search by Year</option>
          </select>

          <button type="submit" className="search-btn">Search</button>
          {isSearching && <button type="button" onClick={clearSearch} className="clear-btn">Clear</button>}
        </form>
      </div>

      {/* Add Book Section */}
      <div className="add-book-section">
        <h2>Add New Book</h2>
        <div className="form-group">
          <input
            type="text"
            placeholder="Title"
            value={newBook.title}
            onChange={(e) => setNewBook({ ...newBook, title: e.target.value })}
          />
          <input
            type="text"
            placeholder="Author"
            value={newBook.author}
            onChange={(e) => setNewBook({ ...newBook, author: e.target.value })}
          />
          <input
            type="text"
            placeholder="ISBN"
            value={newBook.isbn}
            onChange={(e) => setNewBook({ ...newBook, isbn: e.target.value })}
          />
          <input
            type="number"
            placeholder="Published Year"
            value={newBook.publishedYear}
            onChange={(e) => setNewBook({ ...newBook, publishedYear: parseInt(e.target.value) || 0 })}
          />
          <button onClick={addBook} className="add-btn">Add Book</button>
        </div>
      </div>

      {/* Books Display Section */}
      <div className="books-section">
        <h2>Books {isSearching && `(${books.length} found)`}</h2>
        {books.length === 0 ? (
          <p className="no-books">No books found. Try adding one or adjusting your search.</p>
        ) : (
          <ul className="books-list">
            {books.map((book) => (
              <li key={book.id} className="book-item">
                <div className="book-info">
                  <h3>{book.title}</h3>
                  <p><strong>Author:</strong> {book.author}</p>
                  <p><strong>ISBN:</strong> {book.isbn}</p>
                  <p><strong>Published:</strong> {book.publishedYear}</p>
                </div>
                <button
                  onClick={() => book.id && deleteBook(book.id)}
                  className="delete-btn"
                >
                  Delete
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  )
}

export default App
