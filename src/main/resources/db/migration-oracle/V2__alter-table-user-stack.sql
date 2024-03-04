ALTER TABLE
    user_stack
ADD CONSTRAINT
    unique_user_stack
UNIQUE (user_id, stack);