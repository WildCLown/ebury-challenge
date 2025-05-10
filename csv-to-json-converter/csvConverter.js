function convertCsvToJson(file, outputArea) {
  const reader = new FileReader();

  reader.onload = function(event) {
      const csvContent = event.target.result;
      const lines = csvContent.split('\n').filter(line => line.trim() !== ''); // Remove linhas vazias

      const jsonResult = lines.map(line => {
          const [takeOffAirportCode, landingAirportCode, flightCostE2] = line.split(',');

          return {
              takeOffAirportCode: takeOffAirportCode.trim(),
              landingAirportCode: landingAirportCode.trim(),
              flightCostE2: Number(flightCostE2.trim())
          };
      });

      outputArea.value = '';
      outputArea.value = JSON.stringify(jsonResult, null, 2);
  };

  reader.readAsText(file);
}
