import os
import json

def generate_model_files(input_directory, output_directory=None):
    """
    Generate model JSON files from existing JSON files in the input directory.
    
    :param input_directory: Path to the directory containing input JSON files
    :param output_directory: Path to directory for output files (defaults to input directory)
    """
    # If no output directory specified, use the input directory
    if output_directory is None:
        output_directory = input_directory
    
    # Ensure output directory exists
    os.makedirs(output_directory, exist_ok=True)
    
    # Iterate through all JSON files in the input directory
    for filename in os.listdir(input_directory):
        if filename.endswith('.json'):
            input_path = os.path.join(input_directory, filename)
            
            # Remove the .json extension to use as the item name
            item_name = os.path.splitext(filename)[0]
            
            # Create the model JSON structure
            model_data = {
                "model": {
                    "type": "minecraft:model",
                    "model": f"allomancy:item/{item_name}"
                }
            }
            
            # Create output filename
            output_filename = f"{item_name}.json"
            output_path = os.path.join(output_directory, output_filename)
            
            # Write the new JSON file
            with open(output_path, 'w') as output_file:
                json.dump(model_data, output_file, indent=4)
            
            print(f"Generated model file for {item_name}")

# Example usage
if __name__ == "__main__":
    # Replace these with your actual directory paths
    input_dir = "./src/generated/resources/assets/allomancy/models/item"  # Directory containing your original JSON files
    output_dir = "./src/generated/resources/assets/allomancy/items/"  # Directory to save new model JSON files
    
    generate_model_files(input_dir, output_dir)