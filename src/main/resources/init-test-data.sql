WITH new_users AS (
    INSERT
    INTO memorandum.users (username, first_name, last_name, timezone, status, created_on, updated_on)
    VALUES
        ('kyriakos', 'Kyriakos', 'Patsias', 'PST', 'active', now(), now())
    RETURNING id
)
INSERT INTO memorandum.notes (title, content, is_favorite, user_id, status, created_on, updated_on)
SELECT 'Test note', 'Test note body', TRUE, new_users.id, 'active', now(), now()
FROM new_users;