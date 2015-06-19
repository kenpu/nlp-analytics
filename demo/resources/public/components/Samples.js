var React = require('react');

var Samples = React.createClass({
	render: function() {
		var samples = this.props.samples;
		var onChoose = this.props.onChoose;

		var options = samples.map(function(sentence, i) {
			return <option key={i} value={sentence}>{sentence}</option>;
		});

		var choose = function(e) {
			onChoose(e.target.value);
		}
		return (
			<select onChange={choose}>
				{options}
			</select>
		);
	},
});

module.exports = Samples;