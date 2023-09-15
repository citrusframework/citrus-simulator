function setupProxy({ tls }) {
  const serverResources = ['/api', '/services', '/v3/api-docs', '/console'];
  const conf = [
    {
      context: serverResources,
      target: `http${tls ? 's' : ''}://localhost:8080`,
      secure: false,
      changeOrigin: tls,
    },
  ];
  return conf;
}

module.exports = setupProxy;
