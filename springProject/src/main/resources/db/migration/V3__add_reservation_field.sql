-- Create reservation_field table for storing reservation form fields
CREATE TABLE IF NOT EXISTS reservation_field (
    field_id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    input_type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    required BOOLEAN NOT NULL DEFAULT FALSE,
    max_length INTEGER,
    sequence INTEGER NOT NULL,
    CONSTRAINT fk_reservation_field_room
        FOREIGN KEY (room_id)
        REFERENCES room_info(room_id)
        ON DELETE CASCADE
);

-- Add comments for documentation
COMMENT ON TABLE reservation_field IS 'Stores custom fields for reservation form per room';
COMMENT ON COLUMN reservation_field.field_id IS 'Primary key';
COMMENT ON COLUMN reservation_field.room_id IS 'Foreign key to room_info';
COMMENT ON COLUMN reservation_field.title IS 'Field label displayed to user';
COMMENT ON COLUMN reservation_field.input_type IS 'Input type: TEXT, NUMBER, SELECT';
COMMENT ON COLUMN reservation_field.required IS 'Whether field is mandatory';
COMMENT ON COLUMN reservation_field.max_length IS 'Maximum input length';
COMMENT ON COLUMN reservation_field.sequence IS 'Display order';

-- Create index for efficient lookup by room_id
CREATE INDEX IF NOT EXISTS idx_reservation_field_room_id
ON reservation_field(room_id);

-- Create index for ordering by sequence
CREATE INDEX IF NOT EXISTS idx_reservation_field_sequence
ON reservation_field(room_id, sequence);