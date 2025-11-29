-- Add max_occupancy column to room_info table
ALTER TABLE room_info
ADD COLUMN IF NOT EXISTS max_occupancy INTEGER;

-- Add comment for documentation
COMMENT ON COLUMN room_info.max_occupancy IS 'Maximum number of people that can be accommodated in the room';

-- Create index for efficient searching by occupancy
CREATE INDEX IF NOT EXISTS idx_room_info_max_occupancy
ON room_info(max_occupancy)
WHERE max_occupancy IS NOT NULL;

-- Optional: Update existing records with default values if needed
-- UPDATE room_info SET max_occupancy = 4 WHERE max_occupancy IS NULL;