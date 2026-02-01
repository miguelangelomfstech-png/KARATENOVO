function fn() {
  var env = karate.env; // get system property 'karate.env'
  karate.log("karate.env system property was:", env);

  if (!env) {
    env = "dev";
  }

  var config = {
    env: env,
    baseUrl: "https://jsonplaceholder.typicode.com",
  };

  if (env == "dev") {
    // customize
  } else if (env == "qa") {
    // customize
  } else if (env == "staging") {
    // customize
  }

  // connection timeout in milliseconds
  karate.configure("connectTimeout", 5000);
  // read timeout in milliseconds
  karate.configure("readTimeout", 5000);

  return config;
}
