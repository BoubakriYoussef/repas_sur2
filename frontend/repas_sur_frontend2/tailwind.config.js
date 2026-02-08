/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  theme: {
    extend: {
      colors: {
        canvas: '#F6F2EA',
        ink: '#1C1B19',
        muted: '#6B665E',
        card: '#FFFFFF',
        stroke: '#E3DDD2',
        accent: '#2C6E49',
        accent2: '#B85C38',
        night: '#102A24'
      },
      fontFamily: {
        display: ['"Space Grotesk"', 'ui-sans-serif', 'system-ui'],
        body: ['"IBM Plex Sans"', 'ui-sans-serif', 'system-ui']
      },
      boxShadow: {
        soft: '0 10px 30px rgba(17, 24, 39, 0.08)',
        lift: '0 20px 60px rgba(17, 24, 39, 0.12)'
      }
    }
  },
  plugins: []
};
