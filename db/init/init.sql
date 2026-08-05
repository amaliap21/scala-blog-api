-- Mengaktifkan ekstensi UUID (jika menggunakan UUID)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- Tabel Users
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
-- Tabel Posts
CREATE TABLE IF NOT EXISTS posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    author_id UUID NOT NULL,
    CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);