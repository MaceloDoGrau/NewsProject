ALTER TABLE news ADD COLUMN kind VARCHAR(40);
ALTER TABLE news ADD COLUMN summary VARCHAR(300);
ALTER TABLE news ADD COLUMN publication_date DATE;
ALTER TABLE news ADD COLUMN overlay VARCHAR(80);
ALTER TABLE news ADD COLUMN image_id VARCHAR(64);
ALTER TABLE news ADD COLUMN featured BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE news ADD COLUMN popular BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE news ADD COLUMN read_min INTEGER NOT NULL DEFAULT 1;

UPDATE news
SET kind = 'Conteúdo informativo',
    summary = COALESCE(NULLIF(BTRIM(subtitle), ''), title),
    publication_date = COALESCE(CAST(published_at AS DATE), CAST(created_at AS DATE))
WHERE kind IS NULL;

ALTER TABLE news ALTER COLUMN kind SET NOT NULL;
ALTER TABLE news ALTER COLUMN summary SET NOT NULL;
ALTER TABLE news ALTER COLUMN publication_date SET NOT NULL;
ALTER TABLE news ALTER COLUMN title TYPE VARCHAR(140);

ALTER TABLE news DROP COLUMN subtitle;
ALTER TABLE news DROP COLUMN image_url;

ALTER TABLE news ADD CONSTRAINT ck_news_kind CHECK (kind IN (
    'Guia informativo', 'Conteúdo informativo', 'Orientação técnica'
));
ALTER TABLE news ADD CONSTRAINT ck_news_read_min CHECK (read_min >= 1);

CREATE UNIQUE INDEX uk_news_one_featured ON news (featured) WHERE featured;

CREATE TABLE news_images (
    id VARCHAR(64) PRIMARY KEY,
    content_type VARCHAR(80) NOT NULL,
    data BYTEA NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
